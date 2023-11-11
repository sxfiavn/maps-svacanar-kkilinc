import { FeatureCollection } from "geojson";
import { FillLayer } from "react-map-gl";

import rl_data from "../geodata/fullDownload.json";
function isFeatureCollection(json: any): json is FeatureCollection {
  return json.type === "FeatureCollection";
}

export function overlayData(): GeoJSON.FeatureCollection | undefined {
  return isFeatureCollection(rl_data) ? rl_data : undefined;
}

const propertyName = "holc_grade";

export const geoLayer: FillLayer = {
  id: "geo_data",
  type: "fill",
  paint: {
    "fill-color": [
      "match",
      ["get", propertyName],
      "A",
      "#5bcc04",
      "B",
      "#04b8cc",
      "C",
      "#e9ed0e",
      "D",
      "#d11d1d",
      "#ccc",
    ],
    "fill-opacity": 0.3,
  },
};

export const highlightLayerStyle: FillLayer = {
  id: "highlighted_geo_data",
  type: "fill",
  paint: {
    "fill-color": "#ff0000",
    "fill-opacity": 0.5,
  },
};
