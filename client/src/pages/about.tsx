import { Lightbulb, Server, Laptop, Check, Brain, Palette, Copy, Smartphone, Mail, Github } from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent } from "@/components/ui/card";

export default function About() {
  return (
    <div className="min-h-screen bg-background">
      <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
        {/* About Header */}
        <div className="text-center mb-16">
          <h1 className="text-4xl font-bold text-foreground mb-4" data-testid="text-about-title">
            About MailMind
          </h1>
          <p className="text-xl text-muted-foreground">Revolutionizing email communication through artificial intelligence</p>
        </div>

        {/* About Content */}
        <div className="prose prose-lg max-w-none">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-12 items-center mb-16">
            <div>
              <h2 className="text-3xl font-bold text-foreground mb-6">Our Mission</h2>
              <p className="text-muted-foreground mb-4">
                MailMind was created to solve one of the most common challenges in modern business communication: 
                crafting appropriate, professional email replies that match the tone and context of the conversation.
              </p>
              <p className="text-muted-foreground mb-4">
                By leveraging cutting-edge AI technology, we help professionals save time while maintaining 
                high-quality communication standards.
              </p>
              <div className="flex space-x-4 mt-6">
                <div className="bg-primary/10 rounded-lg p-4 flex-1 transition-all duration-300 hover:bg-primary/20 hover:scale-105 hover:shadow-lg cursor-pointer">
                  <div className="text-2xl font-bold text-primary" data-testid="text-stat-emails">10k+</div>
                  <div className="text-sm text-muted-foreground">Emails Generated</div>
                </div>
                <div className="bg-primary/10 rounded-lg p-4 flex-1 transition-all duration-300 hover:bg-primary/20 hover:scale-105 hover:shadow-lg cursor-pointer">
                  <div className="text-2xl font-bold text-primary" data-testid="text-stat-satisfaction">95%</div>
                  <div className="text-sm text-muted-foreground">User Satisfaction</div>
                </div>
              </div>
            </div>
            <div className="bg-gradient-to-br from-primary/10 to-primary/20 rounded-xl p-8 text-center transition-all duration-300 hover:from-primary/20 hover:to-primary/30 hover:scale-105 hover:shadow-xl cursor-pointer group">
              <Lightbulb className="text-primary mx-auto mb-4 transition-transform duration-300 group-hover:scale-110 group-hover:rotate-12" size={64} />
              <h3 className="text-xl font-semibold text-foreground transition-colors duration-300 group-hover:text-primary">Innovation in Communication</h3>
            </div>
          </div>


          {/* Features */}
          <div className="mb-16">
            <h2 className="text-3xl font-bold text-foreground mb-8 text-center">Key Features</h2>
            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              <div className="bg-muted rounded-lg p-6 transition-all duration-300 hover:bg-muted/80 hover:scale-105 hover:shadow-lg cursor-pointer group">
                <h3 className="text-lg font-semibold text-foreground mb-2 flex items-center transition-colors duration-300 group-hover:text-primary">
                  <Brain className="mr-3 text-primary transition-transform duration-300 group-hover:scale-110" size={20} />
                  Smart Context Analysis
                </h3>
                <p className="text-muted-foreground">AI analyzes the email content to understand context and intent.</p>
              </div>
              <div className="bg-muted rounded-lg p-6 transition-all duration-300 hover:bg-muted/80 hover:scale-105 hover:shadow-lg cursor-pointer group">
                <h3 className="text-lg font-semibold text-foreground mb-2 flex items-center transition-colors duration-300 group-hover:text-primary">
                  <Palette className="mr-3 text-primary transition-transform duration-300 group-hover:scale-110" size={20} />
                  Multiple Tone Options
                </h3>
                <p className="text-muted-foreground">Choose from professional, friendly, formal, casual, and more.</p>
              </div>
              <div className="bg-muted rounded-lg p-6 transition-all duration-300 hover:bg-muted/80 hover:scale-105 hover:shadow-lg cursor-pointer group">
                <h3 className="text-lg font-semibold text-foreground mb-2 flex items-center transition-colors duration-300 group-hover:text-primary">
                  <Copy className="mr-3 text-primary transition-transform duration-300 group-hover:scale-110" size={20} />
                  One-Click Copy
                </h3>
                <p className="text-muted-foreground">Instantly copy generated replies to your clipboard.</p>
              </div>
              <div className="bg-muted rounded-lg p-6 transition-all duration-300 hover:bg-muted/80 hover:scale-105 hover:shadow-lg cursor-pointer group">
                <h3 className="text-lg font-semibold text-foreground mb-2 flex items-center transition-colors duration-300 group-hover:text-primary">
                  <Smartphone className="mr-3 text-primary transition-transform duration-300 group-hover:scale-110" size={20} />
                  Responsive Design
                </h3>
                <p className="text-muted-foreground">Works seamlessly across desktop, tablet, and mobile devices.</p>
              </div>
            </div>
          </div>

          {/* Contact */}
          <div className="bg-primary/10 rounded-xl p-8 text-center transition-all duration-300 hover:bg-primary/20 hover:shadow-xl">
            <h2 className="text-2xl font-bold text-foreground mb-4">Get in Touch</h2>
            <p className="text-muted-foreground mb-6">
              Have questions or feedback? We'd love to hear from you!
            </p>
            <div className="flex justify-center space-x-4">
              <Button asChild className="bg-primary hover:bg-primary/90 hover:scale-105 text-primary-foreground font-medium py-2 px-6 transition-all duration-300 hover:shadow-lg" data-testid="button-contact">
                <a href="mailto:support@mailmind.com">
                  <Mail className="mr-2" size={16} />
                  Contact Us
                </a>
              </Button>
              <Button asChild className="bg-secondary hover:bg-secondary/80 hover:scale-105 text-secondary-foreground font-medium py-2 px-6 transition-all duration-300 hover:shadow-lg" data-testid="button-github">
                <a href="https://github.com" target="_blank" rel="noopener noreferrer">
                  <Github className="mr-2" size={16} />
                  View Source
                </a>
              </Button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
