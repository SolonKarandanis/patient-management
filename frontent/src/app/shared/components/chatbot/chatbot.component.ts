import {
  AfterViewChecked,
  Component,
  ElementRef,
  inject,
  signal,
  ViewChild,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { DrawerModule } from 'primeng/drawer';
import { InputTextModule } from 'primeng/inputtext';
import { ScrollPanelModule } from 'primeng/scrollpanel';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { ChatbotService } from '@core/services/chatbot.service';

@Component({
  selector: 'app-chatbot',
  standalone: true,
  imports: [
    FormsModule,
    ButtonModule,
    DrawerModule,
    InputTextModule,
    ScrollPanelModule,
    ProgressSpinnerModule,
  ],
  templateUrl: './chatbot.component.html',
  styleUrls: ['./chatbot.component.css'],
})
export class ChatbotComponent implements AfterViewChecked {
  @ViewChild('messageContainer') private messageContainer!: ElementRef<HTMLDivElement>;

  protected readonly chatbotService = inject(ChatbotService);
  protected inputText = signal('');

  ngAfterViewChecked(): void {
    this.scrollToBottom();
  }

  openPanel(): void {
    this.chatbotService.togglePanel();
    if (this.chatbotService.messages().length === 0) {
      this.chatbotService.loadHistory();
    }
  }

  send(): void {
    const text = this.inputText().trim();
    if (!text || this.chatbotService.isLoading()) return;
    this.inputText.set('');
    this.chatbotService.sendMessage(text);
  }

  onKeydown(event: KeyboardEvent): void {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.send();
    }
  }

  private scrollToBottom(): void {
    try {
      const el = this.messageContainer?.nativeElement;
      if (el) el.scrollTop = el.scrollHeight;
    } catch {}
  }
}
