export interface Section {
  id: string;
  name: string;
  geologicalClasses: GeoClass[];
}

export interface GeoClass {
  id: string;
  name: string;
}

export interface AsyncJob {
  id: string;
  status: 'DONE' | 'IN PROGRESS' | 'ERROR';
}