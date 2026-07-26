import { inject } from '@angular/core';
import { patchState, signalStore, withMethods, withProps, withState } from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { pipe, switchMap, tap } from 'rxjs';
import { tapResponse } from '@ngrx/operators';
import { setError, setLoaded, setLoading, withCallState } from '@core/store/features/call-state.feature';
import { ChatbotDetailState, initialChatbotDetailState } from './chatbot-detail.state';
import { ChatRepository } from '@core/repositories/chat.repository';
import { ChatMessage } from '@models/chat.model';

export const ChatbotDetailStore = signalStore(
  { providedIn: 'root' },
  withState<ChatbotDetailState>(initialChatbotDetailState),
  withCallState(),
  withProps(() => ({
    chatRepo: inject(ChatRepository),
  })),
  withMethods((state) => ({
    setLoadingState() {
      patchState(state, setLoading());
    },
    setLoadedState() {
      patchState(state, setLoaded());
    },
    setErrorState(error: string) {
      patchState(state, setError(error));
    },
    setMessages(messages: ChatMessage[]) {
      patchState(state, { messages });
    },
    appendUserMessage(content: string) {
      patchState(state, { messages: [...state.messages(), { role: 'user', content }] });
    },
    appendAssistantMessage(content: string) {
      patchState(state, { messages: [...state.messages(), { role: 'assistant', content }] });
    },
    clearMessages() {
      patchState(state, { messages: [] });
    },
  })),
  withMethods((state) => {
    const { chatRepo } = state;
    return ({
      clearSession() {
        chatRepo.clearSession().subscribe();
        state.clearMessages();
        state.setLoadedState();
      },
      loadHistory: rxMethod<void>(
        pipe(
          tap(() => state.setLoadingState()),
          switchMap(() =>
            chatRepo.getHistory().pipe(
              tapResponse({
                next: (history) => {
                  const messages: ChatMessage[] = history.messages.map((m) => ({
                    role: m.role.toLowerCase() as 'user' | 'assistant',
                    content: m.content,
                  }));
                  state.setMessages(messages);
                  state.setLoadedState();
                },
                error: () => state.setLoadedState(),
              })
            )
          )
        )
      ),
      sendMessage: rxMethod<string>(
        pipe(
          tap((text) => {
            state.appendUserMessage(text);
            state.setLoadingState();
          }),
          switchMap((text) =>
            chatRepo.sendMessage(text).pipe(
              tapResponse({
                next: (res) => {
                  state.appendAssistantMessage(res.response);
                  state.setLoadedState();
                },
                error: () => {
                  state.setErrorState('Failed to get a response. Please try again.');
                },
              })
            )
          )
        )
      ),
    });
  })
);
