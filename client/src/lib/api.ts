export interface EmailRequest {
  emailContent: string;
  tone: string;
}

export async function generateEmailReply(data: EmailRequest): Promise<string> {
  const response = await fetch('http://localhost:8080/api/email/generate', {
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
