import { patchState, signalStore, withMethods, withState } from '@ngrx/signals';
import { ChatbotUiState, initialChatbotUiState } from './chatbot-ui.state';

export const ChatbotUiStore = signalStore(
  { providedIn: 'root' },
  withState<ChatbotUiState>(initialChatbotUiState),
  withMethods((state) => ({
    togglePanel() {
      patchState(state, { isOpen: !state.isOpen() });
    },
    closePanel() {
      patchState(state, { isOpen: false });
    },
  }))
);
