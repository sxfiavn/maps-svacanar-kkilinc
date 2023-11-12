# maps-svacanar-kkilinc

Sprint 5 - CS32

### Github Repository

https://github.com/sxfiavn/maps-svacanar-kkilinc

### Authors

- Sofia Vaca Narvaja
- Kaan Kilinc

### Collaborators

- Peter Madoff
- Mason Lee (starter code + caching implementation)
- CS32 Staff (Maps Gearup)

### Urban Studies Post Doc Researcher

We catered our application to the Urban Studies Script by implementing the filter and search_area functionality required for this sprint. In addition to that, we also made sure to keep the broadband functionality from the last sprints, and tested it accordingly. We also added a map_key to the commands, which shows the colors and what they mean. Currently the map is only showing redline data, but the map is there if the researcher wanted to add this extra functionality. We put the map and the command/history window side by side so that the filtered data and/or the broadband data could be seen simultaneously. Lastly, we kept accessibility in mind in terms of colors, layout, effects and screen-reader functionality.
For further functionality, we suggest adding some functionality to the map where the user can hover over it and see the coordinates and other details of the area they are hovering on. This would be a good way of further supporting the Post Doc Researcher as it could provide even further connections between their research paper and the web interface.

## File Structure

1. index.tsx: Main entry point of the application.
2. main.ts: Core functionality logic.
3. components/: Directory containing all React components.
4. mocked/: Directory containing mocked data in JSON format.
5. styles/: Directory containing all the stylesheets.

- `App.tsx`: The main application component that organizes the application's structure.
- `REPLHistory`: Maintains a history of user commands and their results.
- `REPLInput`: Handles user input, file loading, and command execution.
- `LoadCSV`: Loads CSV files and provides data to the app.
- `ViewCSV`: Displays the loaded CSV data.
- `mockedJson.ts`: Contains mocked data and search functionality.

1. `load`:
   This function is responsible for loading a CSV file. The function expects the file path as the first argument. Once the file path is provided, the function uses an external API to load the data. If successful, it updates the application state with the loaded data.

2. `view`:
   The view function is tasked with rendering the loaded CSV data. If no data is loaded, it will fetch the data using the ViewCSVApiCall function. If data is loaded, it directly renders the data using the ViewCSV component.

3. `search`:
   The search function allows users to search within the loaded CSV data. It expects a search term and other optional parameters that define the search criteria. The function then uses the SearchCSVWrapper component to perform the search and render the results.

4. `mode`:
   This function enables users to switch between different REPL modes like "verbose" and "brief". It expects a mode value as an argument and updates the application mode based on the provided value.

5. `broadband`:
   This function fetches broadband data for a specific state and county. The state and county values are expected as arguments. The function then uses the broadbandAPI function to fetch and display the data.

6. `mapKey`:
   This function displays a key for the colors in the map and what each of it means.

7. `filter`:
   This function filters the map by the given coordinates.

8. `search_area`:
   This function highlights the area in the map with the keyword in the description.

## Running Instructions

### Frontend

- In terminal run "npm run dev" to open web application.
- Insert commands in command box

### Backend

- In IntelliJ, build project from backend folder and run serverMain.java
- Declare wether you want to use caching or not for GCS API (broad_band) and GeoJSON (filter and search_area)

### Testing

- Testing of previous Sprints are included

## Testing Breakdown

Overview of testing added for this sprint

- Unit testing filter
  - Not enough parameters
  - Too many parameters
  - Invalid parameters
  - No data found
  - Data found (expected successful response)
- Unit testing search_area
  - Message of successful call
  - Not enough parameters
- Random Testing filter
  - Randomly generated coordinates (lat, long)
- Integration testing:
  - filter (data found), search_area, load, view, mode, filter (no data found)
  - filter, search_area, filter again
  - filter, mode, filter, search_area, mode, search_area

## Reflection

Breakdown of what technologies we used:

- Hardware: RAM, CPU, GPU (on M1)
- Back end: IntelliJ IDEA, Node.js, GeoJSON, GCS API, Maven
- Front end: VS Code, React
- Fullstack: Playwright, Github, Git, Typescript, npm
