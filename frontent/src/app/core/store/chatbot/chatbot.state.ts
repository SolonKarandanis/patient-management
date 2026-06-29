import { ChatMessage } from '@models/chat.model';

export interface ChatbotState {
  readonly messages: ChatMessage[];
  readonly sessionId: string;
  readonly isOpen: boolean;
  readonly isLoading: boolean;
  readonly error: string | null;
}

export const initialChatbotState: ChatbotState = {
  messages: [],
  sessionId: crypto.randomUUID(),
  isOpen: false,
  isLoading: false,
  error: null,
};
