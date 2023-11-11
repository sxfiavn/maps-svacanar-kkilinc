// GeoJSONContext.tsx
import React from "react";

interface GeoJSONContextInterface {
  geoJSON: GeoJSON.FeatureCollection | null;
  setGeoJSON: React.Dispatch<
    React.SetStateAction<GeoJSON.FeatureCollection | null>
  >;
}

export const GeoJSONContext =
  React.createContext<GeoJSONContextInterface | null>(null);

  