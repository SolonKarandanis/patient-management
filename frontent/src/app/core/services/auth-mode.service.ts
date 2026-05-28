import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class AuthModeService {
  private readonly _authMode = signal<'jwt' | 'oauth2'>('jwt');
  readonly authMode = this._authMode.asReadonly();

  setMode(mode: 'jwt' | 'oauth2'): void {
    this._authMode.set(mode);
  }

  isOAuth2(): boolean {
    return this._authMode() === 'oauth2';
  }
}
