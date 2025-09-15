import React from "react";
import ThemeToggle from "../../ThemeToggle";
import ProfileMenu from "./ProfileMenu";

export default function Header({ title }) {
  return (
    <header className="bg-white dark:bg-gray-800 shadow-lg border-b border-gray-200 dark:border-gray-700">
      <div className="max-w-7xl mx-auto py-4 px-4 sm:px-6 lg:px-8 flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-semibold ml-14 text-gray-900 dark:text-gray-100">
            {title}
          </h1>
        </div>
        <div className="flex items-center space-x-4">
          <ThemeToggle />
          <ProfileMenu />
        </div>
      </div>
    </header>
  );
}
