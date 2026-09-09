import {Service, signal} from '@angular/core';

@Service()
export class UiService {
  public screenLoaderVisible= signal<boolean>(false);
  public loaderMessage=signal<string|null>(null);

  constructor() { }

  public showScreenLoader():void{
    this.screenLoaderVisible.set(true);
  }

  public showScreenLoaderWithMessage(message:string):void{
    this.screenLoaderVisible.set(true);
    this.loaderMessage.set(message);
  }

  public hideScreenLoader():void{
    this.screenLoaderVisible.set(false);
    this.loaderMessage.set(null);
  }
}
