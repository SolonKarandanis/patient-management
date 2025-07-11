import {AppComponent} from '../src/app/app.component';

describe('AppComponent',()=>{
  it('should mount', () => {
    cy.mount(AppComponent)
  });
})
