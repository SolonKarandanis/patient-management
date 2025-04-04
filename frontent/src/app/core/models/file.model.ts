export interface GenericFile {
  filename: string;
  id: number;
  mimeType?: string;
  size?: number;
  arrayBuffer?: ArrayBuffer;
}

export enum DownloadType {
  CONTRACTS = 'contracts',
  GENERAL = 'general',
}
