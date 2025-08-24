import { useState } from "react";
import { useMutation } from "@tanstack/react-query";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { Edit, Reply, MailOpen, TriangleAlert, Copy, RotateCcw, Bot, Palette, Clock, Loader2 } from "lucide-react";

import { Button } from "@/components/ui/button";
import { Form, FormControl, FormField, FormItem, FormLabel } from "@/components/ui/form";
import { Textarea } from "@/components/ui/textarea";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Card, CardContent } from "@/components/ui/card";
import { useToast } from "@/hooks/use-toast";
import { generateEmailReply, EmailRequest } from "@/lib/api";

const formSchema = z.object({
  emailContent: z.string().min(1, "Email content is required"),
  tone: z.string().default("professional"),
});

type FormData = z.infer<typeof formSchema>;

export default function Home() {
  const [generatedReply, setGeneratedReply] = useState<string>("");
  const { toast } = useToast();

  const form = useForm<FormData>({
    resolver: zodResolver(formSchema),
    defaultValues: {
      emailContent: "",
      tone: "professional",
    },
  });

  const generateMutation = useMutation({
    mutationFn: generateEmailReply,
    onSuccess: (data) => {
      setGeneratedReply(data);
    },
    onError: (error) => {
      toast({
        title: "Generation Failed",
        description: error.message || "Something went wrong. Please try again.",
        variant: "destructive",
      });
    },
  });

  const onSubmit = (data: FormData) => {
    const requestData: EmailRequest = {
      emailContent: data.emailContent,
      tone: data.tone,
    };
    generateMutation.mutate(requestData);
  };

  const copyToClipboard = async () => {
    try {
      await navigator.clipboard.writeText(generatedReply);
      toast({
        title: "Success",
        description: "Reply copied to clipboard!",
      });
    } catch (error) {
      // Fallback for older browsers
      const textArea = document.createElement("textarea");
      textArea.value = generatedReply;
      document.body.appendChild(textArea);
      textArea.select();
      document.execCommand("copy");
      document.body.removeChild(textArea);
      toast({
        title: "Success",
        description: "Reply copied to clipboard!",
      });
    }
  };

  const regenerateReply = () => {
    const formData = form.getValues();
    if (formData.emailContent) {
      onSubmit(formData);
    }
  };

  return (
    <div className="min-h-screen bg-background">
      {/* Hero Section */}
      <div className="bg-gradient-to-r from-primary/10 to-primary/20 py-16">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <h1 className="text-4xl font-bold text-foreground mb-4">
            Generate Perfect Email Replies with AI
          </h1>
          <p className="text-xl text-muted-foreground mb-8">
            Transform your email communication with intelligent, context-aware responses tailored to your preferred tone.
          </p>
        </div>
      </div>

      {/* Main Content */}
      <div className="max-w-6xl mx-auto px-4 sm:px-6 lg:px-8 py-12">
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
          
          {/* Input Section */}
          <Card className="shadow-lg border border-border transition-all duration-300 hover:shadow-xl hover:scale-[1.02] hover:border-primary/20">
            <CardContent className="p-8">
              <h2 className="text-2xl font-semibold text-foreground mb-6 flex items-center">
                <Edit className="mr-3 text-primary" size={24} />
                Compose Your Email
              </h2>
              
              <Form {...form}>
                <form onSubmit={form.handleSubmit(onSubmit)} className="space-y-6">
                  <FormField
                    control={form.control}
                    name="emailContent"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel className="text-sm font-medium text-foreground">
                          Original Email Content *
                        </FormLabel>
                        <FormControl>
                          <Textarea
                            placeholder="Paste the email you want to reply to here..."
                            rows={8}
                            className="resize-none focus:ring-2 focus:ring-primary focus:border-primary"
                            data-testid="textarea-email-content"
                            {...field}
                          />
                        </FormControl>
                      </FormItem>
                    )}
                  />

                  <FormField
                    control={form.control}
                    name="tone"
                    render={({ field }) => (
                      <FormItem>
                        <FormLabel className="text-sm font-medium text-foreground">
                          Reply Tone
                        </FormLabel>
                          <Select onValueChange={field.onChange} value={field.value}>
                              <SelectTrigger
                                  className="focus:ring-2 focus:ring-primary focus:border-primary"
                                  data-testid="select-tone"
                              >
                                  <SelectValue />
                              </SelectTrigger>
                              <SelectContent>
                                  <SelectItem value="professional">Professional (Default)</SelectItem>
                                  <SelectItem value="friendly">Friendly</SelectItem>
                                  <SelectItem value="formal">Formal</SelectItem>
                                  <SelectItem value="casual">Casual</SelectItem>
                                  <SelectItem value="enthusiastic">Enthusiastic</SelectItem>
                                  <SelectItem value="concise">Concise</SelectItem>
                              </SelectContent>
                          </Select>

                      </FormItem>
                    )}
                  />

                  <Button
                    type="submit"
                    disabled={generateMutation.isPending}
                    className="w-full bg-primary hover:bg-primary/90 hover:scale-105 text-primary-foreground font-medium py-3 px-6 transition-all duration-300 hover:shadow-lg"
                    data-testid="button-generate"
                  >
                    {generateMutation.isPending ? (
                      <>
                        <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                        Generating...
                      </>
                    ) : (
                      <>
                        <Bot className="mr-2" size={16} />
                        Generate Reply
                      </>
                    )}
                  </Button>
                </form>
              </Form>
            </CardContent>
          </Card>

          {/* Output Section */}
          <Card className="shadow-lg border border-border">
            <CardContent className="p-8">
              <h2 className="text-2xl font-semibold text-foreground mb-6 flex items-center">
                <Reply className="mr-3 text-primary" size={24} />
                Generated Reply
              </h2>
              
              {!generatedReply && !generateMutation.isPending && (
                <div className="text-center py-12" data-testid="empty-state">
                  <div className="w-16 h-16 mx-auto mb-4 bg-muted rounded-full flex items-center justify-center">
                    <MailOpen className="text-muted-foreground" size={32} />
                  </div>
                  <h3 className="text-lg font-medium text-foreground mb-2">No Reply Generated Yet</h3>
                  <p className="text-muted-foreground">Enter your email content and click "Generate Reply" to get started.</p>
                </div>
              )}

              {generateMutation.isPending && (
                <div className="text-center py-12" data-testid="loading-state">
                  <Loader2 className="h-8 w-8 animate-spin mx-auto mb-4 text-primary" />
                  <p className="text-muted-foreground">Generating your reply...</p>
                </div>
              )}

              {generateMutation.isError && (
                <div className="text-center py-12" data-testid="error-state">
                  <div className="w-16 h-16 mx-auto mb-4 bg-destructive/10 rounded-full flex items-center justify-center">
                    <TriangleAlert className="text-destructive" size={32} />
                  </div>
                  <h3 className="text-lg font-medium text-foreground mb-2">Generation Failed</h3>
                  <p className="text-muted-foreground mb-4">
                    {generateMutation.error?.message || "Something went wrong. Please try again."}
                  </p>
                  <Button
                    onClick={regenerateReply}
                    className="bg-primary hover:bg-primary/90 hover:scale-105 text-primary-foreground transition-all duration-300 hover:shadow-lg"
                    data-testid="button-retry"
                  >
                    Try Again
                  </Button>
                </div>
              )}

              {generatedReply && (
                <div data-testid="reply-content">
                  <div className="bg-muted rounded-lg p-6 mb-4">
                    <div className="text-sm text-muted-foreground mb-2">Generated Reply:</div>
                    <div className="text-foreground whitespace-pre-wrap" data-testid="text-generated-reply">
                      {generatedReply}
                    </div>
                  </div>
                  
                  <div className="flex space-x-3">
                    <Button
                      onClick={copyToClipboard}
                      className="flex-1 bg-green-600 hover:bg-green-700 hover:scale-105 text-white font-medium py-2 px-4 transition-all duration-300 hover:shadow-lg"
                      data-testid="button-copy"
                    >
                      <Copy className="mr-2" size={16} />
                      Copy Reply
                    </Button>
                    <Button
                      onClick={regenerateReply}
                      className="flex-1 bg-secondary hover:bg-secondary/80 hover:scale-105 text-secondary-foreground font-medium py-2 px-4 transition-all duration-300 hover:shadow-lg"
                      data-testid="button-regenerate"
                    >
                      <RotateCcw className="mr-2" size={16} />
                      Regenerate
                    </Button>
                  </div>
                </div>
              )}
            </CardContent>
          </Card>
        </div>

        {/* Features Section */}
        <div className="mt-16">
          <h2 className="text-3xl font-bold text-center text-foreground mb-12">Why Choose MailMind?</h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div className="text-center p-6 transition-all duration-300 hover:scale-105 hover:bg-muted/50 rounded-xl cursor-pointer group">
              <div className="w-16 h-16 mx-auto mb-4 bg-primary/10 rounded-full flex items-center justify-center transition-all duration-300 group-hover:bg-primary/20 group-hover:scale-110">
                <Bot className="text-primary transition-transform duration-300 group-hover:scale-110" size={32} />
              </div>
              <h3 className="text-xl font-semibold text-foreground mb-2 transition-colors duration-300 group-hover:text-primary">AI-Powered</h3>
              <p className="text-muted-foreground">Advanced AI understands context and generates human-like responses.</p>
            </div>
            <div className="text-center p-6 transition-all duration-300 hover:scale-105 hover:bg-muted/50 rounded-xl cursor-pointer group">
              <div className="w-16 h-16 mx-auto mb-4 bg-primary/10 rounded-full flex items-center justify-center transition-all duration-300 group-hover:bg-primary/20 group-hover:scale-110">
                <Palette className="text-primary transition-transform duration-300 group-hover:scale-110" size={32} />
              </div>
              <h3 className="text-xl font-semibold text-foreground mb-2 transition-colors duration-300 group-hover:text-primary">Tone Customization</h3>
              <p className="text-muted-foreground">Choose from multiple tones to match your communication style.</p>
            </div>
            <div className="text-center p-6 transition-all duration-300 hover:scale-105 hover:bg-muted/50 rounded-xl cursor-pointer group">
              <div className="w-16 h-16 mx-auto mb-4 bg-primary/10 rounded-full flex items-center justify-center transition-all duration-300 group-hover:bg-primary/20 group-hover:scale-110">
                <Clock className="text-primary transition-transform duration-300 group-hover:scale-110" size={32} />
              </div>
              <h3 className="text-xl font-semibold text-foreground mb-2 transition-colors duration-300 group-hover:text-primary">Time-Saving</h3>
              <p className="text-muted-foreground">Generate professional replies in seconds, not minutes.</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
