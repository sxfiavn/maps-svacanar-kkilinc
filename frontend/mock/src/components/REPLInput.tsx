import React, { useState } from "react";

import { ControlledInput } from "./ControlledInput";
import { FileLoader } from "./FileLoader";
import ViewCSV from "./ViewCSV";
import { InputObject } from "./types";
import SearchCSV from "./SearchCSV";
import { AvailableFilesList, OnStartUp } from "./FileLoader";
import { fileToSearch } from "../mocked/mockedJson";

interface REPLInputProps {
  onModeChange: (newMode: string) => void;
  onCommandSubmit: (command: InputObject) => void;
  onDataLoad: (data: string[][]) => void;
  loadedData: string[][] | null; // to determine if data is already loaded
}
/**
 * REPLInput component allows users to enter commands, handles command execution,
 * and manages input/output for the REPL.
 * @param props - The component's properties, including handlers for mode change, command submission, data loading, and loaded data status.
 */
export function REPLInput(props: REPLInputProps) {
  const [commandString, setCommandString] = useState<string>("");
  const [loadFile, setLoadFile] = useState<string | null>(null);
  const [loaded, getLoaded] = useState<string>("");

  const handleFileLoad = (data: string[][]) => {
    props.onDataLoad(data);
    setLoadFile(null); // Reset to prevent re-loading
  };

  const handleSubmit = () => {
    const commandParts = commandString.replace(/\s+/g, " ").split(" ");
    //const commandParts = commandString.split(" ");
    const command = commandParts[0].toLowerCase();
    const parameters = commandParts.slice(1);
    let result: string | JSX.Element = ""; //Update the result variable type in REPLInput

    if (command === "load_file") {
      const filePath = parameters[0].toLowerCase();
      if (
        filePath == "path/to/dataset1.csv" ||
        filePath == "path/to/dataset2.csv" ||
        filePath == "path/to/dataset3.csv" ||
        filePath == "path/to/dataset4.csv"
      ) {
        setLoadFile(filePath);
        getLoaded(filePath);
        result = `Loaded CSV data from file: ${filePath}`; // Concatenate filePath with the result
      } else {
        result = "Not a valid file path.";
      }
    } else if (command === "view") {
      if (!props.loadedData) {
        result = "No data loaded. Please use load_file first.";
      } else {
        result = <ViewCSV loadedData={props.loadedData} hasHeader={true} />;
        //result = "Displayed CSV data";
      }
    } else if (command === "search") {
      if (!props.loadedData) {
        result = "No data loaded. Please use load_file before searching.";
      } else {
        const [column, value] = parameters;
        //find which map to use
        const searchMap = fileToSearch[loaded];
        if (searchMap) {
          //find output
          const searchKey = `search ${column.toLowerCase()} ${value.toLowerCase()}`;
          const searchResult = searchMap[searchKey];
          if (searchResult) {
            //result = searchResult.toString();
            result = <ViewCSV loadedData={searchResult} hasHeader={true} />;
          } else {
            result = "No matching results found.";
          }
        } else {
          result = "No matching results found.";
        }
      }
    } else if (command === "mode") {
      const modeValue = parameters[0].toLowerCase();
      const validModes = ["verbose", "brief"];

      if (validModes.includes(modeValue)) {
        props.onModeChange(modeValue);
        result = `Changed mode to '${modeValue}'`;
      } else {
        result = `${modeValue} is not a valid mode`;
      }
    } else if (command === "help") {
      result = <OnStartUp />;
    } else if (command === "search") {
      if (!props.loadedData) {
        result = "No data loaded. Please use load_file before searching.";
      } else {
        result = <SearchCSV loadedData={props.loadedData} />;
      }
    } else {
      result = "Unknown command";
    }

    const newCommand: InputObject = {
      command: commandString,
      result: result,
    };

    props.onCommandSubmit(newCommand);
    setCommandString("");
  };

  return (
    <div className="repl-input">
      {loadFile && (
        <FileLoader filePath={loadFile} onFileLoad={handleFileLoad} />
      )}
      <fieldset>
        <legend>Enter a command:</legend>
        <ControlledInput
          value={commandString}
          setValue={setCommandString}
          ariaLabel="Command input"
        />
      </fieldset>
      <button onClick={handleSubmit}>Submit</button>
    </div>
  );
}
