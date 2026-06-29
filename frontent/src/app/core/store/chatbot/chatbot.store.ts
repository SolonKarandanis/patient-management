import { inject } from '@angular/core';
import { patchState, signalStore, withMethods, withProps, withState } from '@ngrx/signals';
import { rxMethod } from '@ngrx/signals/rxjs-interop';
import { pipe, switchMap, tap } from 'rxjs';
import { tapResponse } from '@ngrx/operators';
import { ChatbotState, initialChatbotState } from './chatbot.state';
import { ChatRepository } from '@core/repositories/chat.repository';

export const ChatbotStore = signalStore(
  { providedIn: 'root' },
  withState<ChatbotState>(initialChatbotState),
  withProps(() => ({
    chatRepo: inject(ChatRepository),
  })),
  withMethods((state) => ({
    togglePanel() {
      patchState(state, { isOpen: !state.isOpen() });
    },
    closePanel() {
      patchState(state, { isOpen: false });
    },
    clearSession() {
      state.chatRepo.clearSession().subscribe();
      patchState(state, {
        messages: [],
        sessionId: crypto.randomUUID(),
        error: null,
      });
    },
    sendMessage: rxMethod<string>(
      pipe(
        tap((text) => {
          patchState(state, {
            messages: [...state.messages(), { role: 'user', content: text }],
            isLoading: true,
            error: null,
          });
        }),
        switchMap((text) =>
          state.chatRepo.sendMessage(text).pipe(
            tapResponse({
              next: (res) => {
                patchState(state, {
                  messages: [...state.messages(), { role: 'assistant', content: res.response }],
                  isLoading: false,
                });
              },
              error: () => {
                patchState(state, {
                  isLoading: false,
                  error: 'Failed to get a response. Please try again.',
                });
              },
            })
          )
        )
      )
    ),
  }))
);
