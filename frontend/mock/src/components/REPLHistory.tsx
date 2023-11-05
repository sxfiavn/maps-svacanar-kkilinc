import { InputObject } from "./types";
import "../styles/main.css";

interface REPLHistoryProps {
  history: InputObject[];
  mode: string;
}
/**
 * REPLHistory component displays the history of commands and their results in the REPL.
 * @param props - The component's properties, including the command history and display mode.
 */
export function REPLHistory(props: REPLHistoryProps) {
  return (
    <div className="repl-history">
      {props.history.map((item, index) => (
        <div key={index}>
          {props.mode === "verbose" ? (
            <div>
              <div>Command: {item.command}</div>
              <div>Output: {item.result}</div>
            </div>
          ) : (
            <div>Result: {item.result}</div>
          )}
        </div>
      ))}
    </div>
  );
}
