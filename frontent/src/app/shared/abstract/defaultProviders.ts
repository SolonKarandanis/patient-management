import { DOCUMENT } from "@angular/common";
import { forwardRef, inject, InjectionToken } from "@angular/core";
import { NG_VALIDATORS, NG_VALUE_ACCESSOR } from "@angular/forms";

export function DEFAULT_VALUE_ACCESSOR(component: any): any {
  return {
    provide: NG_VALUE_ACCESSOR,
    useExisting: forwardRef(() => component),
    multi: true,
  };
}
export function DEFAULT_VALIDATORS(component: any): any {
  return {
    provide: NG_VALIDATORS,
    useExisting: forwardRef(() => component),
    multi: true,
  };
}
// We need to use forwardRef because in ES6 classes are not hoisted to the top,
// so at this point (inside the metadata definition), the class is not yet defined.

// Window token for use constructor(@Inject(WINDOW) window: Window)
export const WINDOW = new InjectionToken<Window>(
  "A reference to the window object",
  {
    factory: () => {
      const { defaultView } = inject(DOCUMENT);

      if (!defaultView) {
        throw new Error("Window is not available");
      }

      return defaultView;
    },
  }
);
