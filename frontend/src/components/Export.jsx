"use client";

import React, { useState } from 'react';
import { api } from '../services/api';

const Export = () => {
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [selectedTable, setSelectedTable] = useState('items');

  const tables = [
    { value: 'items', label: 'Items' },
    { value: 'inventory', label: 'Inventory' },
    { value: 'distributors', label: 'Distributors' },
    { value: 'distributor_prices', label: 'Distributor Prices' }
  ];

  const exportTable = async () => {
    setLoading(true);
    setMessage('Exporting...');
    
    try {
      await api.exportTable(selectedTable);
      setMessage(`${selectedTable} exported successfully!`);
    } catch (error) {
      setMessage('Error exporting table: ' + error.message);
    }
    
    setLoading(false);
  };

  return (
    <div className="p-6">
      <h2 className="text-2xl font-bold mb-4">Data Export</h2>
      
      {message && (
        <div className="mb-4 p-3 bg-blue-100 border border-blue-400 text-blue-700 rounded">
          {message}
        </div>
      )}

      <div className="p-4 border rounded">
        <h3 className="text-lg font-semibold mb-3">Export Database Table</h3>
        
        <div className="flex gap-4 items-center mb-4">
          <label className="font-medium">Select Table:</label>
          <select
            value={selectedTable}
            onChange={(e) => setSelectedTable(e.target.value)}
            className="border px-3 py-2 rounded"
          >
            {tables.map(table => (
              <option key={table.value} value={table.value}>
                {table.label}
              </option>
            ))}
          </select>
          
          <button
            onClick={exportTable}
            disabled={loading}
            className="bg-green-500 text-white px-6 py-2 rounded hover:bg-green-600 disabled:opacity-50"
          >
            {loading ? 'Exporting...' : 'Export CSV'}
          </button>
        </div>
        
        <div className="text-sm text-gray-600">
          <p>This will download a CSV file containing all data from the selected table.</p>
          <p className="mt-1">Available tables: Items, Inventory, Distributors, and Distributor Prices</p>
        </div>
      </div>
    </div>
  );
};

export default Export;
