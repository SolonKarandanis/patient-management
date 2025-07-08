import {Injectable} from '@angular/core';
import {DatePipe} from '@angular/common';
import {Confirmation, ConfirmationService, MessageService, SelectItem} from 'primeng/api';
import {TranslateService} from '@ngx-translate/core';
import {FormGroup} from '@angular/forms';

@Injectable({
  providedIn: 'root'
})
export class UtilService{

  // usefull regexes
  private readonly unamePattern = '^[a-z0-9_-]{8,15}$';
  private readonly mobnumPattern = '^((\\+91-?)|0)?[0-9]{10}$';
  private readonly emailPattern = '^[a-z0-9._%+-]+@[a-z0-9.-]+.[a-z]{2,4}$';
  private readonly pwdPattern = '^(?=.*d)(?=.*[a-z])(?=.*[A-Z])(?!.*s).{6,12}$';
  // At least 1 lowercase alphabetical character, at least 1 uppercase alphabetical character, at least 1 numeric character...
  // ...at least one special character, must be eight characters or longer for Strong strength
  private readonly strongPasswordPattern = '^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[!@#$%^&*"\'()+,-./:;<=>?[\\]^_`{|}~])(?=.{10,})';


  private readonly datePipe = new DatePipe('en');

  constructor(
    private readonly messageService:MessageService,
    private readonly translate:TranslateService,
    private readonly confirmationService:ConfirmationService,
  ) {}

  public get emailRegex():string{
    return this.emailPattern;
  }

  public get passwordRegex():string{
    return this.pwdPattern;
  }

  public get strongPasswordRegex():string{
    return this.strongPasswordPattern;
  }

  public get usernamePattern():string{
    return this.unamePattern;
  }

  public get mobilePhonePattern():string{
    return this.mobnumPattern;
  }

  public deepCopy(toBeCloned:object):object{
    return structuredClone(toBeCloned);
  }

  public shallowCopy(toBeCloned:object):object{
    return Object.assign({}, toBeCloned);
  }

  public nonNullObject(objectToTest:object):boolean{
    return Object.values(objectToTest).every((value)=> value !==null)
  }

  // Function definition with passing two arrays
  public findCommonElement<T>(array1:T[], array2:T[]):boolean {
    // Loop for array1
    for(const element of array1) {
      // Loop for array2
      for (let j = 0; j < array2.length; j++) {
        // Compare the element of each and
        // every element from both of the
        // arrays
        if (element === array2[j]) {
          // Return if common element found
          return true;
        }
      }
    }
    // Return if no common element exist
    return false;
  }

  /**
   * Marks all the controls of a form group as dirty
   * @param formGroup The form group that contains the controls to be marked as dirty
   */
  markAllAsDirty(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach((key: string) => {
      formGroup.controls[key].markAsDirty();
      formGroup.controls[key].markAsTouched({ onlySelf: true });
    });
  }

  /**
   * Marks all the controls of a form group as pristine
   * @param formGroup The form group that contains the controls to be marked as pristine
   */
  markAllAsPristine(formGroup: FormGroup): void {
    Object.keys(formGroup.controls).forEach((key: string) => {
      formGroup.controls[key].markAsPristine();
      formGroup.controls[key].markAsUntouched({ onlySelf: true });
    });
  }

  /**
   * Convert an Object to a SelectItem for use with PrimeNG components
   * @param obj The Object to convert
   * @param labelAttribute The object's attribute to use as a label
   * @returns The SelectItem based on the original Object
   */
  convertObjectToSelectItem(obj: any, labelAttribute: string): SelectItem {
    return {
      value: obj,
      label: obj[labelAttribute],
    };
  }

  /**
   * Convert an array of Objects to an array of SelectItems for use with PrimeNG components
   * @param obj The array of Objects to convert
   * @param labelAttribute The object's attribute to use as a label
   * @returns The array of SelectItems based on the original array of Objects
   */
  convertObjectArrToSelectItemArr(obj: any[], labelAttribute: string): SelectItem[] {
    return obj.map((obj: any) => this.convertObjectToSelectItem(obj, labelAttribute));
  }

  /**
   * Download a file.
   *
   * @param data - Array Buffer data
   * @param type - the MIME type of the file.
   * @param filename - The file's name.
   */
  triggerFileDownLoad(data: any, type: string, filename: string) {
    const blob = new Blob([data], { type });
    const url = window.URL.createObjectURL(blob);

    const a = document.createElement('a');
    a.setAttribute('style', 'display:none');
    a.href = url;
    a.download = filename;
    a.click();
    a.remove();
  }

  /**
   * Creates a Blob object from a JSON object
   * @param object The JSON to convert
   * @returns The Blob
   */
  createJsonBlob(object: any): Blob {
    return new Blob([JSON.stringify(object)], {
      type: 'application/json',
    });
  }

  /**
   * Convert a Date object to the Airbnb specific format (this function is a wrapper of the Angular Date Pipe)
   * @param date The date object to convert
   * @returns The converted date string or null
   */
  convertDateObjectsToAppFormat(date: Date): string | null {
    return this.datePipe.transform(date, 'dd-MM-yyyy HH:mm:ss Z');
  }

  /**
   * Convert a Date object to the CCM specific format (this function is a wrapper of the Angular Date Pipe)
   * @param date The date object to convert
   * @returns The converted date string or null
   */
  convertDateObjectsToAppReportFormat(date: Date): string | null {
    return this.datePipe.transform(date, 'dd/MM/yyyy');
  }

  /**
   * Convert a Date string to the CCM specific format needed for PrimeNG calendars
   * @param date The date string to convert
   * @returns The converted date string or null
   */
  convertDateStringToCalendarFormat(date: string): string | null {
    return this.convertDateObjectsToAppReportFormat(new Date(date));
  }



  showMessage(severity:SeverityMessage, details:string|string[]):void{
    if(!Array.isArray(details)){
      details =[details];
    }

    for(const detail of details){
      switch(severity){
        case 'error':{
          this.messageService.add({
            severity:'error',
            summary: this.translate.instant('GLOBAL.ERRORS.summary'),
            detail
          });
          break;
        }
        case 'success':{
          this.messageService.add({
            severity:'success',
            summary: this.translate.instant('GLOBAL.SUCCESS.summary'),
            detail
          });
          break;
        }
        case 'warn':{
          this.messageService.add({
            severity:'warn',
            summary: this.translate.instant('GLOBAL.WARNING.summary'),
            detail
          });
          break;
        }
      }
    }
  }

  showConfirmation(confirmation: Confirmation):void{
    this.confirmationService.confirm(confirmation);
  }

  /**
   * Checks if the provided objects are equal
   * @param objects An array of JS objects
   * @returns Whether the provided objects are equal
   */
  isEqualObjects(...objects: object[]): boolean {
    return objects.every((obj) => JSON.stringify(obj) === JSON.stringify(objects[0]));
  }

  /**
   * Convert a UInt8 array to Base64
   * @param arr The array to convert
   * @returns A Base64 string based on the input array
   */
  convertUint8ToBase64(arr: Uint8Array): string {
    return btoa(
      Array(arr.length)
        .fill('')
        .map((_, i) => String.fromCharCode(arr[i]))
        .join('')
    );
  }

  getSelectedItemsLabels(items: SelectItem[], selectedValues: any[]): string {
    return items
      .filter((item) => selectedValues.includes(item.value))
      .map((item) => item.label)
      .join(',');
  }

  /**
   * Convert a value into a string
   * @param value The value to be converted
   * @returns A string representation of the input value
   */
  getValueAsStringOrNA(value: ValueAsStringOrNA): string {
    if (value !== undefined && value !== null) {
      return value as string;
    } else {
      return this.translate.instant('GLOBAL.OTHER.not-available');
    }
  }

  isDecimalNumber(value: any): boolean {
    return !isNaN(parseFloat(value)) && isFinite(value) && typeof value !== 'string' && !Number.isInteger(value);
  }

  megabytesToBytes(megabytes: number): number {
    return megabytes * 1024 * 1024;
  }
}

type ValueAsStringOrNA = string | number | boolean | null | undefined;

type SeverityMessage = 'error' | 'success' | 'warn';
