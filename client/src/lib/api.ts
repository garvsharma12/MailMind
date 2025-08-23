import { apiRequest } from "./queryClient";

export interface EmailRequest {
  emailContent: string;
  tone: string;
}

export async function generateEmailReply(data: EmailRequest): Promise<string> {
  const response = await apiRequest('POST', '/api/email/generate', data);
  return response.text();
}
