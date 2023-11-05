import React, { useEffect } from "react";
import { mockDataMap } from "../mocked/mockedJson";
import { FileLoaderProps } from "./types";

export const FileLoader: React.FC<FileLoaderProps> = ({
  filePath,
  onFileLoad,
}) => {
  const preprocessFilePath = (path: string): string | null => {
    // Step 1: Trim leading and trailing white spaces
    let processedPath = path.trim();

    // Step 2: Validate against an empty string
    if (processedPath === "") {
      console.error("Error: The file path is empty after trimming.");
      return null;
    }

    // Step 3: Remove leading and trailing quotes if present
    processedPath = processedPath.replace(/^["']|["']$/g, "");

    // Step 4: Replace multiple slashes with a single slash
    processedPath = processedPath.replace(/\/+/g, "/");

    // Step 5: Convert backslashes to forward slashes
    processedPath = processedPath.replace(/\\/g, "/");

    // Step 6: Replace multiple spaces with a single space
    processedPath = processedPath.replace(/\s+/g, " ");

    // Step 7: Remove any harmful sequences or characters
    processedPath = processedPath.replace(/(\.\.\/)|(~\/)|(\.$)/g, "");

    // Step 8: Check for special characters that might be unsafe
    // const specialCharacters = /[!@#$%^&*()+{};':"|,.<>?]+/;
    // if (specialCharacters.test(processedPath)) {
    //   console.error("Error: The file path contains special characters.");
    //   return null;
    // }

    // Step 9: Check for directory climbing attempts
    if (processedPath.includes("..")) {
      console.error(
        "Error: The file path is trying to escape the root directory."
      );
      return null;
    }

    // Step 10: Check for valid file formats
    const validFileFormats = /.*\.(csv|json)$/;
    if (!validFileFormats.test(processedPath)) {
      console.error("Error: Invalid file format.");
      return null;
    }

    return processedPath;
  };

  const loadData = () => {
    const processedFilePath = preprocessFilePath(filePath);
    if (processedFilePath) {
      const data = mockDataMap[processedFilePath];
      if (data) {
        onFileLoad(data);
      } else {
        console.error("Error: File not found in mockDataMap.");
      }
    }
  };

  useEffect(() => {
    loadData();
  }, [filePath]);

  const processedFilePath = preprocessFilePath(filePath);

  return (
    <div>
      {processedFilePath ? (
        <p>Processed File Path: {processedFilePath}</p>
      ) : (
        <p>Error: Invalid File Path</p>
      )}
    </div>
  );
};
/**
 * AvailableFilesList component for displaying available files.
 */
export const AvailableFilesList: React.FC = () => {
  // Check if mockDataMap is not null or undefined
  if (!mockDataMap) {
    return <div>Error: Data not available.</div>;
  }

  const availableFiles = Object.keys(mockDataMap);

  // Check if availableFiles array is empty
  if (availableFiles.length === 0) {
    return <div>No files available.</div>;
  }

  return (
    <ul>
      {availableFiles.map((filePath, index) =>
        // Check if filePath is not null or empty
        filePath ? (
          <li key={index}>{filePath}</li>
        ) : (
          <li key={index}>Unknown File</li>
        )
      )}
    </ul>
  );
};
/**
 * OnStartUp component for displaying the welcome message and available commands.
 */
export const OnStartUp: React.FC = () => {
  return (
    <div>
      <h2>Welcome to our REPL</h2>
      <p>
        This REPL allows you to interact with data files, view their contents,
        search them, and change the display mode.
      </p>
      <h3>Available Files:</h3>
      <AvailableFilesList />
      <h3>Valid Commands:</h3>
      <ul>
        <li>
          <strong>load_file [filePath]</strong>: Load a CSV data from the
          specified file.
          <br />
          <em>Example:</em> <code>load_file /path/to/myfile.csv</code>
        </li>
        <li>
          <strong>view</strong>: Display the currently loaded CSV data.
          <br />
          <em>Example:</em> <code>view</code>
        </li>
        <li>
          <strong>search [column] [value]</strong>: Search for a specific value
          in the specified column of the loaded data.
          <br />
          <em>Example:</em> <code>search name John</code>
        </li>
        <li>
          <strong>mode [verbose/brief]</strong>: Change the display mode of the
          REPL.
          <br />
          <em>Example:</em> <code>mode verbose</code>
        </li>
      </ul>
    </div>
  );
};
