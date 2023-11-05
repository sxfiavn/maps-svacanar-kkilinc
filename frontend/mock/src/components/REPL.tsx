import { useState } from "react";
import "../styles/main.css";
import { REPLHistory } from "./REPLHistory";
import { REPLInput } from "./REPLInput";
import { InputObject } from "./types";
import { AvailableFilesList, OnStartUp } from "./FileLoader";
/**
 * REPL component acts as the main container for the Read-Eval-Print Loop (REPL) application.
 */
export default function REPL() {
  const [commandHistory, setCommandHistory] = useState<InputObject[]>([
    {
      command: "Welcome Message:",
      result: <OnStartUp />,
    },
  ]);
  const [mode, setMode] = useState<string>("brief");
  const [loadedData, setLoadedData] = useState<string[][] | null>(null); // NEW: state to store loaded data

  const handleDataLoad = (data: string[][]) => {
    // NEW: handler for loaded data
    setLoadedData(data);
  };

  const handleChangeMode = (newMode: string) => {
    setMode(newMode);
  };

  const handleCommandSubmit = (command: InputObject) => {
    if (command) {
      setCommandHistory((prevHistory) => [...prevHistory, command]);
    } else {
      console.error("Invalid command");
    }
  };

  return (
    <div className="repl">
      <REPLHistory history={commandHistory} mode={mode} />
      <hr></hr>
      <REPLInput
        onModeChange={handleChangeMode}
        onCommandSubmit={handleCommandSubmit}
        onDataLoad={handleDataLoad}
        loadedData={loadedData} // NEW: passing loaded data down to REPLInput
      />
    </div>
  );
}
