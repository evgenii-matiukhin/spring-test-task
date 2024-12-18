import { UUID } from "node:crypto";

export interface Section {
  id: bigint;
  name: string;
  geologicalClasses: GeoClass[];
}

export interface GeoClass {
  id: bigint;
  name: string;
  code: string;
}

export interface AsyncJob {
  id: UUID;
  status: 'DONE' | 'IN_PROGRESS' | 'ERROR';
}

export interface SectionPage {
  content: Section[],
  empty: boolean,
  first: boolean,
  last: boolean,
  number: number,
  numberOfElements: number,
  size: number,
  totalElements: bigint,
  totalPages: number,
  sort: object
}