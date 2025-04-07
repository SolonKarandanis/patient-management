import {Injectable} from '@angular/core';
import { JwtPayload } from "jsonwebtoken";
import {User} from '@models/user.model';

@Injectable({
  providedIn: 'root',
})
export class JwtUtil{
  private readonly ID_TOKEN_KEY = "token" as string;
  private readonly ID_TOKEN_KEY_EXPIRATION = "expires" as string;

  /**
   * @description get token form localStorage
   */
  public getToken():string | null {
    return window.sessionStorage.getItem(this.ID_TOKEN_KEY);
  }

  /**
   * @description save token into localStorage
   * @param token
   */
  public saveToken(token: string):void{
    window.sessionStorage.setItem(this.ID_TOKEN_KEY, token);
  }

  /**
   * @description remove token form localStorage
   */
  public destroyToken():void{
    window.sessionStorage.removeItem(this.ID_TOKEN_KEY);
  }

  /**
   * @description get token expiration form localStorage
   */
  public getTokenExpiration():string | null{
    return window.sessionStorage.getItem(this.ID_TOKEN_KEY_EXPIRATION);
  }

  /**
   * @description save token expiration into localStorage
   * @param expires
   */
  public saveTokenExpiration(expires: string):void{
    window.sessionStorage.setItem(this.ID_TOKEN_KEY_EXPIRATION, expires);
  }

  /**
   * @description remove token expiration form localStorage
   */
  public destroyTokenExpiration():void{
    window.sessionStorage.removeItem(this.ID_TOKEN_KEY_EXPIRATION);
  }

  /**
   * Checks if the JWT has expired
   * @returns If the JWT has expired
   */
  public isJwtExpired(): boolean {
    const jwt: JwtPayload | null = this.parseJwtAsPayload(this.getToken());

    if (jwt) {
      const expDate: Date = new Date(jwt.exp! * 1000);
      const nowDate: Date = new Date();
      const isExpired = expDate < nowDate;
      return isExpired;
    } else {
      return true;
    }
  }

  /**
   * Parses the token as Users
   * @param token The string that contains the encoded JWT contents
   * @returns The decoded Users object
   */
  public getUser(token: string | null):User | null{
    if (!token) {
      return null;
    }
    const jsonPayload = this.getPayLoad(token);
    return JSON.parse(jsonPayload) as User;
  }


  /**
   * Parses the token as JwtPayload
   * @param token The string that contains the encoded JWT contents
   * @returns The decoded JWT object
   */
  private parseJwtAsPayload(token: string | null): JwtPayload | null {
    if (!token) {
      return null;
    }
    const jsonPayload = this.getPayLoad(token);
    return JSON.parse(jsonPayload);
  }

  /**
   * Decodes a JWT
   * @param token The string that contains the encoded JWT contents
   * @returns The decoded JWT string
   */
  private getPayLoad(token: string):string{
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      window
        .atob(base64)
        .split('')
        .map(function (c) {
          return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        })
        .join('')
    );
    return jsonPayload;
  }


}
