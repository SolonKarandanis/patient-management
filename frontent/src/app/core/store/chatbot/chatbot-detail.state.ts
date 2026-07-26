import { ChatMessage } from '@models/chat.model';

export type ChatbotDetailState = {
  readonly messages: ChatMessage[];
}

export const initialChatbotDetailState: ChatbotDetailState = {
  messages: [],
};
