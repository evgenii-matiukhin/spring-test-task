export interface Section {
  id: number | undefined;
  name: string;
  geologicalClasses: GeoClass[];
}

export interface GeoClass {
  id?: number | undefined;
  name: string;
  code: string;
}

export interface AsyncJob {
  id: string;
  status: 'DONE' | 'IN PROGRESS' | 'ERROR';
}

export interface PageRequest {
  page: number;
  size: number;
}

export interface SectionPage {
  content: Section[];
  empty: boolean;
  first: boolean;
  last: boolean;
  number: number;
  numberOfElements: number;
  size: number;
  totalElements: bigint;
  totalPages: number;
  sort: object;
}