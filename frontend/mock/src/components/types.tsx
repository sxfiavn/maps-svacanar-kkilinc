// types.ts
/**
 * Interface representing an input object, which contains a command and its result.
 */
export interface InputObject {
  command: string;
  result: string | JSX.Element;
}

/**
 * Interface for the props of the SearchCSV component.
 */
export interface SearchCSVProps {
  loadedData: string[][] | null;
}

/**
 * Interface for the props of the FileLoader component.
 */
export interface FileLoaderProps {
  filePath: string;
  onFileLoad: (data: string[][]) => void; // Callback to return the data to the parent component
}