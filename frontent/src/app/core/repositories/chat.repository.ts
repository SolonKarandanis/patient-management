import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { BaseRepository } from './BaseRepository';
import { ApiRepositories } from './ApiRepositories';
import { ChatRequest, ChatResponse } from '@models/chat.model';

@Injectable({ providedIn: 'root' })
export class ChatRepository extends BaseRepository {

  sendMessage(message: string): Observable<ChatResponse> {
    const body: ChatRequest = { message };
    return this.http.post<ChatResponse>(`${ApiRepositories.AUTH}/${ApiRepositories.CHAT}`, body);
  }

  clearSession(): Observable<void> {
    return this.http.delete<void>(`${ApiRepositories.AUTH}/${ApiRepositories.CHAT}`);
  }
}
