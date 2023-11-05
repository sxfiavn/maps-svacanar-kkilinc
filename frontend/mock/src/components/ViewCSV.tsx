import React from "react";

interface ViewCSVProps {
  loadedData: string[][] | null;
  hasHeader: boolean; // Add this line for the new boolean prop
}

const ViewCSV: React.FC<ViewCSVProps> = ({ loadedData, hasHeader }) => {
  if (!loadedData) return null;

  const startIndex = hasHeader ? 1 : 0; // Decide where to start rendering from based on the hasHeader flag

  return (
    <table>
      {hasHeader && (
        <thead>
          <tr>
            {loadedData[0].map((header, index) => (
              <th key={index}>{header}</th>
            ))}
          </tr>
        </thead>
      )}
      <tbody>
        {loadedData.slice(startIndex).map((row, rowIndex) => (
          <tr key={rowIndex}>
            {row.map((cell, cellIndex) => (
              <td key={cellIndex}>{cell}</td>
            ))}
          </tr>
        ))}
      </tbody>
    </table>
  );
};

ViewCSV.defaultProps = {
  hasHeader: true,
};

export default ViewCSV;
