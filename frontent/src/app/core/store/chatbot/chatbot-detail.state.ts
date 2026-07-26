import { ChatMessage } from '@models/chat.model';

export type ChatbotDetailState = {
  readonly messages: ChatMessage[];
  readonly isLoading: boolean;
  readonly error: string | null;
}

export const initialChatbotDetailState: ChatbotDetailState = {
  messages: [],
  isLoading: false,
  error: null,
};
