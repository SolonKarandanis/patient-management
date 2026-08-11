import {
  AfterViewChecked,
  Component,
  ElementRef,
  inject,
  signal,
  ViewChild,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ChatbotService } from '@core/services/chatbot.service';
import {HlmButtonImports} from '@components/ui/button';
import {HlmInputImports} from '@components/ui/input';
import {HlmSheetImports} from '@components/ui/sheet';
import {HlmSpinnerImports} from '@components/ui/spinner';
import {NgIcon, provideIcons} from '@ng-icons/core';
import {lucideMessageCircle, lucideSend, lucideTrash2} from '@ng-icons/lucide';
import type {BrnDialogState} from '@spartan-ng/brain/dialog';

@Component({
  selector: 'app-chatbot',
  standalone: true,
  imports: [
    FormsModule,
    HlmButtonImports,
    HlmInputImports,
    HlmSheetImports,
    HlmSpinnerImports,
    NgIcon,
  ],
  providers: [provideIcons({lucideMessageCircle, lucideSend, lucideTrash2})],
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

  protected handleSheetStateChanged(state: BrnDialogState): void {
    if (state === 'closed' && this.chatbotService.isOpen()) {
      this.chatbotService.closePanel();
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
