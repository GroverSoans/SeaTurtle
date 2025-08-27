"use client";

import React, { useState, useEffect } from 'react';
import { api } from '../services/api';

const Distributors = () => {
  // State for distributors and their items
  const [distributors, setDistributors] = useState([]);
  const [distributorItems, setDistributorItems] = useState({});
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  
  // Form state for various operations
  const [newDistributor, setNewDistributor] = useState({ name: '' });
  const [newItem, setNewItem] = useState({ distributorId: '', itemId: '', cost: '' });
  const [updatePrice, setUpdatePrice] = useState({ distributorId: '', itemId: '', cost: '' });
  const [restockQuery, setRestockQuery] = useState({ itemId: '', quantity: '' });

  // Load all distributors and their items
  const loadDistributors = async () => {
    setLoading(true);
    try {
      const data = await api.getAllDistributors();
      setDistributors(data);
      setMessage('Distributors loaded successfully');
      // Load items for each distributor
      await loadAllDistributorItems(data);
    } catch (error) {
      setMessage('Error loading distributors: ' + error.message);
    }
    setLoading(false);
  };

  // Load items for all distributors
  const loadAllDistributorItems = async (distributorsList) => {
    const itemsMap = {};
    for (const distributor of distributorsList) {
      try {
        const items = await api.getDistributorItems(distributor.id);
        itemsMap[distributor.id] = items;
      } catch (error) {
        console.error(`Error loading items for distributor ${distributor.id}:`, error);
        itemsMap[distributor.id] = [];
      }
    }
    setDistributorItems(itemsMap);
  };

  // Load items for a specific distributor
  const loadDistributorItems = async (distributorId) => {
    try {
      const items = await api.getDistributorItems(distributorId);
      setDistributorItems(prev => ({
        ...prev,
        [distributorId]: items
      }));
    } catch (error) {
      console.error('Error loading distributor items:', error);
    }
  };

  // Add new distributor
  const addDistributor = async () => {
    if (!newDistributor.name) {
      setMessage('Distributor name is required');
      return;
    }
    
    setLoading(true);
    try {
      const result = await api.addDistributor(newDistributor.name);
      if (result.id) {
        setMessage('Distributor added successfully');
        setNewDistributor({ name: '' });
        loadDistributors();
      } else {
        setMessage('Error: ' + (result.error || 'Failed to add distributor'));
      }
    } catch (error) {
      setMessage('Error adding distributor: ' + error.message);
    }
    setLoading(false);
  };

  // Add item to distributor's catalog
  const addItemToDistributor = async () => {
    if (!newItem.distributorId || !newItem.itemId || !newItem.cost) {
      setMessage('All fields are required');
      return;
    }
    
    setLoading(true);
    try {
      const result = await api.addItemToDistributor(
        parseInt(newItem.distributorId),
        parseInt(newItem.itemId),
        parseFloat(newItem.cost)
      );
      if (result.message) {
        setMessage('Item added to distributor successfully');
        setNewItem({ distributorId: '', itemId: '', cost: '' });
        // Reload items for this distributor
        await loadDistributorItems(parseInt(newItem.distributorId));
      } else {
        setMessage('Error: ' + (result.error || 'Failed to add item'));
      }
    } catch (error) {
      setMessage('Error adding item: ' + error.message);
    }
    setLoading(false);
  };

  // Update item price for a distributor
  const updateItemPrice = async () => {
    if (!updatePrice.distributorId || !updatePrice.itemId || !updatePrice.cost) {
      setMessage('All fields are required');
      return;
    }
    
    setLoading(true);
    try {
      const result = await api.updateItemPrice(
        parseInt(updatePrice.distributorId),
        parseInt(updatePrice.itemId),
        parseFloat(updatePrice.cost)
      );
      if (result.message) {
        setMessage('Price updated successfully');
        setUpdatePrice({ distributorId: '', itemId: '', cost: '' });
        // Reload items for this distributor
        await loadDistributorItems(parseInt(updatePrice.distributorId));
      } else {
        setMessage('Error: ' + (result.error || 'Failed to update price'));
      }
    } catch (error) {
      setMessage('Error updating price: ' + error.message);
    }
    setLoading(false);
  };

  // Get restock price for an item
  const getRestockPrice = async () => {
    if (!restockQuery.itemId || !restockQuery.quantity) {
      setMessage('Item ID and quantity are required');
      return;
    }
    
    setLoading(true);
    try {
      const result = await api.getRestockPrice(
        parseInt(restockQuery.itemId),
        parseInt(restockQuery.quantity)
      );
      if (result.cheapestOption) {
        setMessage(`Cheapest restock: ${result.cheapestOption.distributorName} - $${result.cheapestOption.totalCost.toFixed(2)}`);
      } else {
        setMessage('Error: ' + (result.error || 'Failed to get restock price'));
      }
    } catch (error) {
      setMessage('Error getting restock price: ' + error.message);
    }
    setLoading(false);
  };

  // Delete distributor
  const deleteDistributor = async (id) => {
    if (!confirm('Are you sure you want to delete this distributor?')) return;
    
    setLoading(true);
    try {
      const result = await api.deleteDistributor(id);
      if (result.message) {
        setMessage('Distributor deleted successfully');
        loadDistributors();
      } else {
        setMessage('Error: ' + (result.error || 'Failed to delete distributor'));
      }
    } catch (error) {
      setMessage('Error deleting distributor: ' + error.message);
    }
    setLoading(false);
  };

  // Load data on component mount
  useEffect(() => {
    loadDistributors();
  }, []);

  return (
    <div className="p-6">
      <h2 className="text-2xl font-bold mb-4">Distributor Management</h2>
      
      {/* Success/Error message display */}
      {message && (
        <div className="mb-4 p-3 bg-blue-100 border border-blue-400 text-blue-700 rounded">
          {message}
        </div>
      )}

      {/* Add New Distributor */}
      <div className="mb-6 p-4 border rounded">
        <h3 className="text-lg font-semibold mb-3">Add New Distributor</h3>
        <div className="flex gap-2 mb-2">
          <input
            type="text"
            placeholder="Distributor name"
            value={newDistributor.name}
            onChange={(e) => setNewDistributor({ ...newDistributor, name: e.target.value })}
            className="border px-3 py-2 rounded flex-1"
          />
          <button
            onClick={addDistributor}
            disabled={loading}
            className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 disabled:opacity-50"
          >
            Add Distributor
          </button>
        </div>
      </div>

      {/* Add Item to Distributor */}
      <div className="mb-6 p-4 border rounded">
        <h3 className="text-lg font-semibold mb-3">Add Item to Distributor</h3>
        <div className="flex gap-2 mb-2">
          <input
            type="number"
            placeholder="Distributor ID"
            value={newItem.distributorId}
            onChange={(e) => setNewItem({ ...newItem, distributorId: e.target.value })}
            className="border px-3 py-2 rounded w-32"
          />
          <input
            type="number"
            placeholder="Item ID"
            value={newItem.itemId}
            onChange={(e) => setNewItem({ ...newItem, itemId: e.target.value })}
            className="border px-3 py-2 rounded w-24"
          />
          <input
            type="number"
            step="0.01"
            placeholder="Cost"
            value={newItem.cost}
            onChange={(e) => setNewItem({ ...newItem, cost: e.target.value })}
            className="border px-3 py-2 rounded w-24"
          />
          <button
            onClick={addItemToDistributor}
            disabled={loading}
            className="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600 disabled:opacity-50"
          >
            Add Item
          </button>
        </div>
        <p className="text-sm text-gray-600 mt-2">
          Add an item that a distributor sells and set its cost.
        </p>
      </div>

      {/* Update Item Price */}
      <div className="mb-6 p-4 border rounded">
        <h3 className="text-lg font-semibold mb-3">Update Item Price</h3>
        <div className="flex gap-2 mb-2">
          <input
            type="number"
            placeholder="Distributor ID"
            value={updatePrice.distributorId}
            onChange={(e) => setUpdatePrice({ ...updatePrice, distributorId: e.target.value })}
            className="border px-3 py-2 rounded w-32"
          />
          <input
            type="number"
            placeholder="Item ID"
            value={updatePrice.itemId}
            onChange={(e) => setUpdatePrice({ ...updatePrice, itemId: e.target.value })}
            className="border px-3 py-2 rounded w-24"
          />
          <input
            type="number"
            step="0.01"
            placeholder="New Cost"
            value={updatePrice.cost}
            onChange={(e) => setUpdatePrice({ ...updatePrice, cost: e.target.value })}
            className="border px-3 py-2 rounded w-24"
          />
          <button
            onClick={updateItemPrice}
            disabled={loading}
            className="bg-yellow-500 text-white px-4 py-2 rounded hover:bg-yellow-600 disabled:opacity-50"
          >
            Update Price
          </button>
        </div>
        <p className="text-sm text-gray-600 mt-2">
          Update the cost of an item that a distributor sells.
        </p>
      </div>

      {/* Restock Price Query */}
      <div className="mb-6 p-4 border rounded">
        <h3 className="text-lg font-semibold mb-3">Get Restock Price</h3>
        <div className="flex gap-2 mb-2">
          <input
            type="number"
            placeholder="Item ID"
            value={restockQuery.itemId}
            onChange={(e) => setRestockQuery({ ...restockQuery, itemId: e.target.value })}
            className="border px-3 py-2 rounded w-24"
          />
          <input
            type="number"
            placeholder="Quantity"
            value={restockQuery.quantity}
            onChange={(e) => setRestockQuery({ ...restockQuery, quantity: e.target.value })}
            className="border px-3 py-2 rounded w-24"
          />
          <button
            onClick={getRestockPrice}
            disabled={loading}
            className="bg-purple-500 text-white px-4 py-2 rounded hover:bg-purple-600 disabled:opacity-50"
          >
            Get Price
          </button>
        </div>
        <p className="text-sm text-gray-600 mt-2">
          Find the cheapest distributor for restocking a specific item quantity.
        </p>
      </div>

      {/* Distributors List */}
      <div className="mb-6">
        <div className="flex justify-between items-center mb-3">
          <h3 className="text-lg font-semibold">Current Distributors</h3>
          <button
            onClick={loadDistributors}
            disabled={loading}
            className="bg-gray-500 text-white px-3 py-1 rounded hover:bg-gray-600 disabled:opacity-50"
          >
            Refresh
          </button>
        </div>
        
        {loading ? (
          <div className="text-center py-4">Loading...</div>
        ) : (
          <div className="border rounded">
            <table className="w-full">
              <thead className="bg-gray-50">
                <tr>
                  <th className="px-4 py-2 text-left">ID</th>
                  <th className="px-4 py-2 text-left">Name</th>
                  <th className="px-4 py-2 text-left">Actions</th>
                </tr>
              </thead>
              <tbody>
                {distributors.map((distributor) => (
                  <tr key={distributor.id} className="border-t">
                    <td className="px-4 py-2">{distributor.id}</td>
                    <td className="px-4 py-2">{distributor.name}</td>
                    <td className="px-4 py-2">
                      <button
                        onClick={() => deleteDistributor(distributor.id)}
                        className="bg-red-500 text-white px-2 py-1 rounded text-sm hover:bg-red-600"
                      >
                        Delete
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        )}
      </div>

      {/* Distributor Items and Prices */}
      <div className="mb-6">
        <div className="flex justify-between items-center mb-3">
          <h3 className="text-lg font-semibold">Distributor Items & Prices</h3>
          <button
            onClick={() => loadDistributors()}
            disabled={loading}
            className="bg-gray-500 text-white px-3 py-1 rounded hover:bg-gray-600 disabled:opacity-50"
          >
            Refresh All
          </button>
        </div>
        
        {distributors.map((distributor) => (
          <div key={distributor.id} className="mb-4 border rounded">
            <div className="bg-gray-50 px-4 py-2 border-b">
              <h4 className="font-semibold">
                {distributor.name} (ID: {distributor.id})
              </h4>
            </div>
            
            {distributorItems[distributor.id] && distributorItems[distributor.id].length > 0 ? (
              <div className="p-4">
                <table className="w-full">
                  <thead className="bg-gray-100">
                    <tr>
                      <th className="px-4 py-2 text-left">Item ID</th>
                      <th className="px-4 py-2 text-left">Item Name</th>
                      <th className="px-4 py-2 text-left">Cost</th>
                    </tr>
                  </thead>
                  <tbody>
                    {distributorItems[distributor.id].map((item) => (
                      <tr key={`${distributor.id}-${item.id}`} className="border-t">
                        <td className="px-4 py-2">{item.id}</td>
                        <td className="px-4 py-2">{item.name || 'N/A'}</td>
                        <td className="px-4 py-2">${item.cost?.toFixed(2) || 'N/A'}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <div className="p-4 text-gray-500">
                No items available from this distributor.
              </div>
            )}
          </div>
        ))}
      </div>
    </div>
  );
};

export default Distributors;
