import { inject, Injectable } from '@angular/core';
import { ChatbotUiStore } from '@core/store/chatbot/chatbot-ui.store';
import { ChatbotDetailStore } from '@core/store/chatbot/chatbot-detail.store';

@Injectable({
  providedIn: 'root',
})
export class ChatbotService {

  private chatbotUiStore = inject(ChatbotUiStore);
  private chatbotDetailStore = inject(ChatbotDetailStore);

  public isOpen = this.chatbotUiStore.isOpen;
  public messages = this.chatbotDetailStore.messages;
  public isLoading = this.chatbotDetailStore.isLoading;
  public error = this.chatbotDetailStore.error;

  public togglePanel(): void {
    this.chatbotUiStore.togglePanel();
  }

  public closePanel(): void {
    this.chatbotUiStore.closePanel();
  }

  public clearSession(): void {
    this.chatbotDetailStore.clearSession();
  }

  public loadHistory(): void {
    this.chatbotDetailStore.loadHistory(undefined);
  }

  public sendMessage(text: string): void {
    this.chatbotDetailStore.sendMessage(text);
  }
}
