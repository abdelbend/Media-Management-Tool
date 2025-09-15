
# Frontend Project with ReactJS

Welcome to the Frontend Project! This README outlines the guidelines for contributing to this project, specifically regarding the organization of components, styles, and assets. Following these conventions will help maintain a clean and manageable codebase.

## Table of Contents

- [Project Setup](#project-setup)
- [Component Structure](#component-structure)
- [Styling Components](#styling-components)
- [Adding Assets](#adding-assets)
- [Contributing](#contributing)
- [License](#license)

---

## Project Setup

To get started with this project, ensure you have [Node.js](https://nodejs.org/) and [npm](https://www.npmjs.com/) installed. Follow the steps below to set up the project:

1. Clone the repository:
   ```bash
   git clone https://your-repo-url.git
   ```

2. Navigate to the project directory:
   ```bash
   cd your-project-directory
   ```

3. Install the required dependencies:
   ```bash
   npm install
   ```

4. Start the development server:
   ```bash
   npm run dev
   ```

---

## Component Structure

When adding new components to this project, please adhere to the following conventions:


### **Component Location**
- All React component files should be placed inside the `src/components` folder.
- Components should be organized into subfolders based on their category or purpose:
  - **common**: For reusable components used across multiple parts of the application.
  - **media**: For components specifically related to media functionalities or features.

### **Page Location**
- Page-specific components should be placed directly inside the `src` folder in **PascalCase** format.

### **File Naming**: Each component should have a corresponding `.jsx` file, named in PascalCase. For example:
    - If your component is named `LoginForm`, the file should be named `LoginForm.jsx`.

Example:
```
src/
└── components/
    ├── common/            
    │   ├── Sidebar.jsx
    │   ├── StateCard.jsx
    ├── media/      
    │   ├── MediaTable.jsx
    │   ├── MediaCard.jsx
    ├── OTHER COMPONENTS/      
├── pages/             
    │   ├── LoginPage.jsx
    │   ├── MediaPage.jsx
    │   ├── OverviewPage.jsx

```

---

## Styling Components

For styling your components, please follow these guidelines:

- **CSS File Location**: Place your CSS files in the same directory as the component they style, within the `src/components` folder.
- **File Naming**: Name the CSS file in lowercase, matching the component name. For example:
    - For `LoginForm.jsx`, the CSS file should be named `loginForm.css`.

Example:
```
src/
└── components/
    ├── LoginForm.jsx
    ├── loginForm.css
    └── Header.jsx
```

---

## Adding Assets

When adding images, icons, or any other assets to the project:

- **Asset Location**: Save all asset files in the `src/assets` folder.
- **File Naming**: Use descriptive filenames in PascalCase for assets to maintain consistency and ease of access.

Example:
```
src/
└── assets/
    ├── Logo.png
    ├── Background.jpg
    └── Icons/
        ├── SearchIcon.svg
        └── UserIcon.svg

```

---

