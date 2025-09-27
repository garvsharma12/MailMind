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
}

