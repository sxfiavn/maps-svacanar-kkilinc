import { render } from "react-dom";
import "../styles/main.css";
import { Component } from "react";

interface REPLHistoryProps {
  history: string[][][];
}
// This keeps track of the REPL history: a table of arrays
export function REPLHistory(props: REPLHistoryProps) {
  return (
    <div
      className="repl-history"
      tabIndex={0}
      aria-live="polite"
      aria-label="History"
      aria-description="Area with the history of the outputs and commands"
    >
      {props.history.map((command, index) => (
        <div>
          <Table body={command} />
        </div>
      ))}
    </div>
  );

  // This denotes a table as an input with table row Mapped over each row
  function Table({ body }: { body: string[][] }) {
    return (
      <table style={{ width: 500 }} className="center" aria-label={"Table"}>
        <tbody>
          {body.map((rowContent, rowID) => (
            <TableRow rowContent={rowContent} />
          ))}
        </tbody>
      </table>
    );
  }

  // This converts each row into a list of table datums
  function TableRow({ rowContent }: { rowContent: string[] }) {
    let row = rowContent;
    return (
      <tr aria-label={"row"}>
        {row.map((val, rowID) => (
          <td aria-label={"column"} key={rowID}>
            {val}
          </td>
        ))}
      </tr>
    );
  }
}
