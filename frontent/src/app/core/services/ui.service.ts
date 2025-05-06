import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root'
})
export class UiService {
  public screenLoaderVisible:boolean = false;
  public loaderMessage:string|null = null;

  constructor() { }

  public showScreenLoader():void{
    this.screenLoaderVisible = true;
  }

  public showScreenLoaderWithMessage(message:string):void{
    this.screenLoaderVisible = true;
    this.loaderMessage=message;
  }

  public hideScreenLoader():void{
    this.screenLoaderVisible = false;
    this.loaderMessage=null;
  }
}
