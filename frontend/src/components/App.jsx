"use client";

import React, { useState } from 'react';
import Inventory from './Inventory';
import Distributors from './Distributors';
import Queries from './Queries';
import Export from './Export';

const App = () => {
  // Track which section is currently active
  const [activeSection, setActiveSection] = useState('inventory');

  // Render the appropriate component based on active section
  const renderSection = () => {
    switch (activeSection) {
      case 'inventory':
        return <Inventory />;
      case 'distributors':
        return <Distributors />;
      case 'queries':
        return <Queries />;
      case 'export':
        return <Export />;
      default:
        return <Inventory />;
    }
  };

  return (
    <div className="min-h-screen bg-gray-50">
      {/* Top navigation bar */}
      <nav className="bg-white shadow-sm border-b">
        <div className="max-w-7xl mx-auto px-4">
          <div className="flex space-x-8">
            <button
              onClick={() => setActiveSection('inventory')}
              className={`py-4 px-2 border-b-2 font-medium text-sm ${
                activeSection === 'inventory'
                  ? 'border-blue-500 text-blue-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              }`}
            >
              Inventory
            </button>
            <button
              onClick={() => setActiveSection('distributors')}
              className={`py-4 px-2 border-b-2 font-medium text-sm ${
                activeSection === 'distributors'
                  ? 'border-blue-500 text-blue-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              }`}
            >
              Distributors
            </button>
            <button
              onClick={() => setActiveSection('queries')}
              className={`py-4 px-2 border-b-2 font-medium text-sm ${
                activeSection === 'queries'
                  ? 'border-blue-500 text-blue-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              }`}
            >
              Queries
            </button>
            <button
              onClick={() => setActiveSection('export')}
              className={`py-4 px-2 border-b-2 font-medium text-sm ${
                activeSection === 'export'
                  ? 'border-blue-500 text-blue-600'
                  : 'border-transparent text-gray-500 hover:text-gray-700 hover:border-gray-300'
              }`}
            >
              Export
            </button>
          </div>
        </div>
      </nav>

      {/* Main content area */}
      <main className="max-w-7xl mx-auto py-6">
        {renderSection()}
      </main>
    </div>
  );
};

export default App;
