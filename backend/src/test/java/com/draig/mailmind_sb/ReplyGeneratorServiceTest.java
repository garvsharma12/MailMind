package com.draig.mailmind_sb;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class ReplyGeneratorServiceTest {

    private String callSanitize(String output, String original) throws Exception {
        ReplyGeneratorService svc = new ReplyGeneratorService(org.springframework.web.reactive.function.client.WebClient.builder());
        Method m = ReplyGeneratorService.class.getDeclaredMethod("sanitizeModelOutput", String.class, String.class);
        m.setAccessible(true);
        return (String) m.invoke(svc, output, original);
    }

    @Test
    void removesOriginalBlockAndQuotedLines() throws Exception {
        String original = "Hello team,\nPlease find the attached report.\nRegards, Alice";
        String modelOutput = "-----8<----- BEGIN ORIGINAL EMAIL -----\n" + original + "\n-----8<----- END ORIGINAL EMAIL -----\n\n> Hello team,\n> quoted line\n\nHi Alice,\nThanks for sending the report. I'll review it and get back to you by EOD.\n\nBest regards,\nBob";

        String cleaned = callSanitize(modelOutput, original);
        assertFalse(cleaned.contains("BEGIN ORIGINAL EMAIL"));
        assertFalse(cleaned.contains("END ORIGINAL EMAIL"));
        assertFalse(cleaned.contains("quoted line"));
        assertFalse(cleaned.contains(original));
        assertTrue(cleaned.startsWith("Hi Alice"));
        assertTrue(cleaned.contains("Best regards"));
    }

    @Test
    void fallsBackIfOverStripped() throws Exception {
        String original = "This is a unique long line that might get removed";
        String modelOutput = original; // if model only echoed
        String cleaned = callSanitize(modelOutput, original);
        assertEquals(modelOutput.trim(), cleaned);
    }

    @Test
    void removesCommonHeaderAndQuotedBlocks() throws Exception {
        String original = "Meeting tomorrow at 10am. Please confirm.";
        String modelOutput = "On Mon, Sep 1, Alice wrote:\n> Meeting tomorrow at 10am. Please confirm.\nFrom: Alice <alice@example.com>\nSubject: Re: Meeting\n\nHi Alice,\nThanks for the reminder. I confirm my availability at 10am tomorrow.\n\nBest,\nBob";
        String cleaned = callSanitize(modelOutput, original);
        assertFalse(cleaned.contains("On Mon"));
        assertFalse(cleaned.contains("From:"));
        assertFalse(cleaned.contains("Subject:"));
        assertTrue(cleaned.startsWith("Hi Alice"));
        assertTrue(cleaned.contains("confirm my availability"));
    }

    @Test
    void removesLinesMostlyCopiedFromOriginal() throws Exception {
        String original = "Hello team,\nPlease find the attached report for Q3 performance.\nRegards, Alice";
        String modelOutput = "Please find the attached report for Q3 performance.\n\nHi Alice,\nAppreciate the update. I'll review the Q3 report and follow up with any questions by Friday.\n\nBest regards,\nBob";
        String cleaned = callSanitize(modelOutput, original);
        assertFalse(cleaned.contains("Please find the attached report for Q3 performance."));
        assertTrue(cleaned.startsWith("Hi Alice"));
        assertTrue(cleaned.contains("Best regards"));
    }
}
