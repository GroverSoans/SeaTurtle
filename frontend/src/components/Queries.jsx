"use client";

import React, { useState } from 'react';
import { api } from '../services/api';

const Queries = () => {
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  const [results, setResults] = useState(null);
  const [queryType, setQueryType] = useState('outOfStock');

  const queries = [
    { id: 'outOfStock', label: 'Out of Stock Items', func: api.getOutOfStock },
    { id: 'overstocked', label: 'Overstocked Items', func: api.getOverstocked },
    { id: 'lowStock', label: 'Low Stock Items', func: api.getLowStock },
    { id: 'allDistributors', label: 'All Distributors', func: api.getAllDistributors }
  ];

  const runQuery = async () => {
    setLoading(true);
    setMessage('');
    setResults(null);
    
    try {
      // Find the selected query function
      const selectedQuery = queries.find(q => q.id === queryType);
      if (!selectedQuery) {
        setMessage('Invalid query type selected');
        return;
      }
      
      const data = await selectedQuery.func();
      setResults(data);
      setMessage(`Query executed successfully. Found ${Array.isArray(data) ? data.length : 0} results.`);
    } catch (error) {
      setMessage('Error executing query: ' + error.message);
    }
    
    setLoading(false);
  };

  return (
    <div className="p-6">
      <h2 className="text-2xl font-bold mb-4">Data Queries</h2>
      
      {message && (
        <div className="mb-4 p-3 bg-blue-100 border border-blue-400 text-blue-700 rounded">
          {message}
        </div>
      )}

      <div className="mb-6 p-4 border rounded">
        <h3 className="text-lg font-semibold mb-3">Run Predefined Queries</h3>
        
        <div className="flex gap-4 items-center mb-4">
          <label className="font-medium">Select Query:</label>
          <select
            value={queryType}
            onChange={(e) => setQueryType(e.target.value)}
            className="border px-3 py-2 rounded"
          >
            {queries.map(query => (
              <option key={query.id} value={query.id}>
                {query.label}
              </option>
            ))}
          </select>
          
          <button
            onClick={runQuery}
            disabled={loading}
            className="bg-blue-500 text-white px-6 py-2 rounded hover:bg-blue-600 disabled:opacity-50"
          >
            {loading ? 'Running...' : 'Run Query'}
          </button>
        </div>
        
        <div className="text-sm text-gray-600">
          <p>Select a query type and click "Run Query" to execute it against the database.</p>
        </div>
      </div>

      {/* Results Display */}
      {results && (
        <div className="mb-6">
          <h3 className="text-lg font-semibold mb-3">Query Results</h3>
          
          <div className="border rounded overflow-hidden">
            <table className="w-full">
              <thead className="bg-gray-50">
                <tr>
                  {results.length > 0 && Object.keys(results[0]).map(key => (
                    <th key={key} className="px-4 py-2 text-left text-sm font-medium text-gray-700">
                      {key.charAt(0).toUpperCase() + key.slice(1)}
                    </th>
                  ))}
                </tr>
              </thead>
              <tbody>
                {results.map((item, index) => (
                  <tr key={index} className="border-t bg-white">
                    {Object.values(item).map((value, valueIndex) => (
                      <td key={valueIndex} className="px-4 py-2 text-sm text-gray-900">
                        {value}
                      </td>
                    ))}
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}
    </div>
  );
};

export default Queries;
