import { HttpClient } from "@angular/common/http";
import { inject } from "@angular/core";

export class BaseRepository{

  private _http = inject(HttpClient);

  protected get http():HttpClient{
    return this._http;
  }
}
