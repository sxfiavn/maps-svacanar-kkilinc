// App.tsx
import React, { useState } from "react";
import { GeoJSONContext } from "./GeoJSONContext";
import MapBox from "./MapBox";
import REPL from "./REPL";
import "../styles/App.css"; // Import your styles here

function App() {
  const [geoJSON, setGeoJSON] = useState<GeoJSON.FeatureCollection | null>(
    null
  );

  return (
    <GeoJSONContext.Provider value={{ geoJSON, setGeoJSON }}>
      <div className="App">
        <header className="App-header">
          <h1 tabIndex={0}>URBAN STUDIES RESEARCH</h1>
        </header>
        <div className="map-repl">
          <REPL />
          <MapBox />
        </div>
      </div>
    </GeoJSONContext.Provider>
  );
}

export default App;
