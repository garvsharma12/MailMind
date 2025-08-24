import { Link, useLocation } from "wouter";
import { Brain } from "lucide-react";
import { ThemeToggle } from "@/components/ui/theme-toggle";

export function Navigation() {
  const [location] = useLocation();

  return (
    <nav className="bg-background border-b border-border sticky top-0 z-50 backdrop-blur supports-[backdrop-filter]:bg-background/60">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between items-center h-16">
          <div className="flex items-center">
            <div className="flex-shrink-0">
              <h1 className="text-2xl font-bold text-primary flex items-center cursor-pointer transition-all duration-300 hover:scale-105 hover:text-primary/80">
                <Brain className="mr-2 transition-transform duration-300 hover:rotate-12" size={24} />
                MailMind
              </h1>
            </div>
          </div>
          <div className="flex items-center space-x-6">
            <div className="flex space-x-8">
              <Link href="/" data-testid="link-home">
                <span
                  className={`pb-4 px-1 text-sm font-medium transition-all duration-300 ${
                    location === "/"
                      ? "text-primary border-b-2 border-primary"
                      : "text-muted-foreground hover:text-foreground hover:scale-105"
                  }`}
                >
                  Home
                </span>
              </Link>
              <Link href="/about" data-testid="link-about">
                <span
                  className={`pb-4 px-1 text-sm font-medium transition-all duration-300 ${
                    location === "/about"
                      ? "text-primary border-b-2 border-primary"
                      : "text-muted-foreground hover:text-foreground hover:scale-105"
                  }`}
                >
                  About
                </span>
              </Link>
            </div>
            <ThemeToggle />
          </div>
        </div>
      </div>
    </nav>
  );
}
