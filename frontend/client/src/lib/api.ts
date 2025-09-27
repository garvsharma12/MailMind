export interface EmailRequest {
  emailContent: string;
  tone: string;
  // Optional length preference: "short" | "medium" | "long"
  length?: "short" | "medium" | "long";
}

const API_BASE: string = (import.meta as any).env?.VITE_API_BASE_URL ?? "";

function joinUrl(base: string, path: string) {
  if (!base) return path;
  return `${base.replace(/\/$/, "")}${path.startsWith("/") ? "" : "/"}${path}`;
}

export async function generateEmailReply(data: EmailRequest): Promise<string> {
  const url = joinUrl(API_BASE, "/api/email/generate");
  const response = await fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(data),
  });
  
  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }
  
  return response.text();
}
