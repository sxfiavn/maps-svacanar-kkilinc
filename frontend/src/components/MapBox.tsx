import Map, { Layer, Source, MapRef, MapLayerMouseEvent } from "react-map-gl";
import React, { useEffect, useState, useRef, useContext } from "react";
import { Feature, Polygon, MultiPolygon, FeatureCollection } from "geojson";
import { GeoJSONContext } from "./GeoJSONContext";
import { highlightLayerStyle, geoLayer } from "./overlays";
import { ACCESS_TOKEN } from "../private/api";
import "../styles/index.css";
import * as mapboxgl from "mapbox-gl";

// interface that mantains the altitude and longetude
interface LatLong {
  lat: number;
  long: number;
}

// interface that mantains the information that may show up on a popup
interface PopupInfo {
  area_description_data?: string;
  city?: string;
  holc_grade?: string;
  holc_id?: string;
  neighborhood_id?: string;
  state?: string;
  latitude: number;
  longitude: number;
}

function isFeatureCollectionV1(json: any): json is FeatureCollection {
  const result = json.type === "FeatureCollection";
  if (!result) {
    console.log(
      "isFeatureCollectionV1: Input JSON is not a FeatureCollection:",
      json
    );
  }
  return result;
}

export function overlayDataV1(
  data: any
): GeoJSON.FeatureCollection | undefined {
  if (isFeatureCollectionV1(data)) {
    return data;
  } else {
    console.log("overlayDataV1: Input data is not a FeatureCollection:", data);
    return undefined;
  }
}

// Utility functions to calculate bounds
function isLngLatTuple(coord: any): coord is [number, number] {
  return (
    Array.isArray(coord) &&
    coord.length === 2 &&
    typeof coord[0] === "number" &&
    typeof coord[1] === "number"
  );
}

function getBoundsOfFeature(feature: Feature<Polygon | MultiPolygon>) {
  const bounds = new mapboxgl.LngLatBounds();

  const coordinates = feature.geometry.coordinates;

  coordinates.forEach((coord) => {
    if (Array.isArray(coord) && coord.every(isLngLatTuple)) {
      coord.forEach((lngLat) => {
        bounds.extend(lngLat);
      });
    } else {
      coord.forEach((linearRing) => {
        if (Array.isArray(linearRing) && linearRing.every(isLngLatTuple)) {
          linearRing.forEach((lngLat) => {
            bounds.extend(lngLat);
          });
        }
      });
    }
  });

  return bounds;
}

function MapBox() {
  const ProvidenceLatLong: LatLong = { lat: 41.824, long: -71.4128 };
  const initialZoom = 12;
  const mapRef = useRef<MapRef>(null);
  const [popupInfo, setPopupInfo] = useState<PopupInfo | null>(null);
  const [isMapLoaded, setIsMapLoaded] = useState(false);
  const [viewState, setViewState] = useState({
    longitude: ProvidenceLatLong.long,
    latitude: ProvidenceLatLong.lat,
    zoom: initialZoom,
  });
  const [overlay, setOverlay] = useState<GeoJSON.FeatureCollection | undefined>(
    undefined
  );

  // fetches the geojson data from the backend for the overlay
  useEffect(() => {
    const fetchData = async () => {
      if (mapRef.current) {
        const bounds = mapRef.current.getMap().getBounds();
        const minLatitude = bounds.getSouthWest().lat;
        const minLongitude = bounds.getSouthWest().lng;
        const maxLatitude = bounds.getNorthEast().lat;
        const maxLongitude = bounds.getNorthEast().lng;
        const response = await fetch(
          "http://localhost:8080/filter?lowerX=" +
            minLongitude +
            "&upperX=" +
            maxLongitude +
            "&lowerY=" +
            minLatitude +
            "&upperY=" +
            maxLatitude
        );
        const data = await response.json();
        if (data["result"] === "success") {
          setOverlay(data["geojson"]);
        }
      }
    };
    fetchData();
  }, [isMapLoaded]);

  const context = useContext(GeoJSONContext);
  const geoJSON = context?.geoJSON;

  useEffect(() => {
    if (
      geoJSON &&
      isFeatureCollectionV1(geoJSON) &&
      geoJSON.features.length > 0
    ) {
      const firstFeature = geoJSON.features[0];
      if (
        firstFeature.geometry.type === "Polygon" ||
        firstFeature.geometry.type === "MultiPolygon"
      ) {
        const bounds = getBoundsOfFeature(
          firstFeature as Feature<Polygon | MultiPolygon>
        );
        setViewState((prevState) => ({
          ...prevState,
          longitude: bounds.getCenter().lng,
          latitude: bounds.getCenter().lat,
          zoom: 10,
        }));
      }
    }
  }, [geoJSON, setViewState]);

  // checks to see if the object is of type PopupInfo
  function isPopupInfo(properties: any): properties is PopupInfo {
    return properties !== null && typeof properties === "object";
  }

  // handles user click on map
  function onMapClick(e: MapLayerMouseEvent) {
    if (mapRef.current) {
      const bbox: [[number, number], [number, number]] = [
        [e.point.x - 5, e.point.y - 5],
        [e.point.x + 5, e.point.y + 5],
      ];

      const features = mapRef.current.queryRenderedFeatures(bbox, {
        layers: ["geo_data"],
      });

      if (features.length > 0) {
        const feature = features[0];
        const properties = feature.properties;
        if (isPopupInfo(properties)) {
          setPopupInfo({
            ...properties,
            latitude: e.lngLat.lat,
            longitude: e.lngLat.lng,
          });
        }
      }
    }
  }

  // sets the state of the infoBox
  const closeInfoBox = () => {
    setPopupInfo(null);
  };

  // sets the state of the map
  const onMapLoad = () => {
    setIsMapLoaded(true);
  };

  return (
    <div className="MapBox">
      <Map
        mapboxAccessToken={ACCESS_TOKEN}
        {...viewState}
        onMove={(ev) => setViewState(ev.viewState)}
        style={{ width: "100%", height: "100%" }}
        mapStyle="mapbox://styles/mapbox/light-v11"
        onClick={onMapClick}
        onLoad={onMapLoad}
        ref={mapRef}
      >
        {/* Base map overlay */}
        <Source id="geodata" type="geojson" data={overlay}>
          <Layer {...geoLayer} />
        </Source>

        {/* Highlighted regions layer */}
        {geoJSON && (
          <Source
            id="highlighted_geo_data"
            type="geojson"
            data={overlayDataV1(geoJSON)}
          >
            {/* The highlightLayerStyle will be rendered on top of red_line_data by default */}
            <Layer {...highlightLayerStyle} />
          </Source>
        )}

        {popupInfo && (
          <div className="infoBox">
            <div>
              <strong>Area:</strong> {popupInfo.area_description_data}
            </div>
            <div>
              <strong>City:</strong> {popupInfo.city}
            </div>
            <div>
              <strong>State:</strong> {popupInfo.state}
            </div>
            <div>
              <strong>HOLC Gracde:</strong> {popupInfo.holc_grade}
            </div>
            <div>
              <strong>Neighbourhood ID:</strong> {popupInfo.neighborhood_id}
            </div>
            <div>
              <strong>Latitude/Longitude:</strong>{" "}
              {popupInfo.latitude.toFixed(4)}
              {"/"}
              {popupInfo.longitude.toFixed(4)}
            </div>

            <button className="closing" onClick={closeInfoBox}>
              Close
            </button>
          </div>
        )}
      </Map>
    </div>
  );
}

export default MapBox;
