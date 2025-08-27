"use client";

import React, { useState, useEffect } from 'react';
import { api } from '../services/api';

const Inventory = () => {
  // State for inventory data and UI
  const [inventory, setInventory] = useState([]);
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState('');
  
  // Form state for adding/updating items
  const [newItem, setNewItem] = useState({ name: '', itemId: '', stock: '', capacity: '' });
  const [updateItem, setUpdateItem] = useState({ itemId: '', stock: '', capacity: '' });

  // Load inventory data from API
  const loadInventory = async () => {
    setLoading(true);
    try {
      const data = await api.getAllInventory();
      setInventory(data);
      setMessage('Inventory loaded successfully');
    } catch (error) {
      setMessage('Error loading inventory: ' + error.message);
    }
    setLoading(false);
  };

  // Load all items from catalog
  const loadItems = async () => {
    try {
      const data = await api.getAllItems();
      setItems(data);
    } catch (error) {
      console.error('Error loading items:', error);
    }
  };

  // Add new item to catalog
  const addItem = async () => {
    if (!newItem.name) {
      setMessage('Item name is required');
      return;
    }
    
    setLoading(true);
    try {
      const result = await api.addNewItem(newItem.name);
      if (result.id) {
        setMessage('Item added successfully');
        setNewItem({ name: '', itemId: '', stock: '', capacity: '' });
        loadItems(); // Reload items list
        loadInventory(); // Reload inventory
      } else {
        setMessage('Error: ' + (result.error || 'Failed to add item'));
      }
    } catch (error) {
      setMessage('Error adding item: ' + error.message);
    }
    setLoading(false);
  };

  // Add item to inventory with stock and capacity
  const addToInventory = async () => {
    if (!newItem.itemId || !newItem.stock || !newItem.capacity) {
      setMessage('Item ID, Stock, and Capacity are required');
      return;
    }
    
    setLoading(true);
    try {
      const result = await api.addToInventory(
        parseInt(newItem.itemId),
        parseInt(newItem.stock),
        parseInt(newItem.capacity)
      );
      if (result.message) {
        setMessage('Item added to inventory successfully');
        setNewItem({ name: '', itemId: '', stock: '', capacity: '' });
        loadInventory();
      } else {
        setMessage('Error: ' + (result.error || 'Failed to add to inventory'));
      }
    } catch (error) {
      setMessage('Error adding to inventory: ' + error.message);
    }
    setLoading(false);
  };

  // Update existing inventory item
  const updateInventoryItem = async () => {
    if (!updateItem.itemId || !updateItem.stock || !updateItem.capacity) {
      setMessage('All fields are required');
      return;
    }
    
    setLoading(true);
    try {
      console.log('Calling updateInventoryItem with:', updateItem); // Debug log
      const result = await api.updateInventoryItem(
        parseInt(updateItem.itemId),
        parseInt(updateItem.stock),
        parseInt(updateItem.capacity)
      );
      if (result.message) {
        setMessage('Inventory updated successfully');
        setUpdateItem({ itemId: '', stock: '', capacity: '' });
        loadInventory();
      } else {
        setMessage('Error: ' + (result.error || 'Failed to update inventory'));
      }
    } catch (error) {
      console.error('Update error:', error); // Debug log
      setMessage('Error updating inventory: ' + error.message);
    }
    setLoading(false);
  };

  // Delete inventory item
  const deleteItem = async (id) => {
    if (!confirm('Are you sure you want to delete this item?')) return;
    
    setLoading(true);
    try {
      const result = await api.deleteInventoryItem(id);
      if (result.message) {
        setMessage('Item deleted successfully');
        loadInventory();
      } else {
        setMessage('Error: ' + (result.error || 'Failed to delete item'));
      }
    } catch (error) {
      setMessage('Error deleting item: ' + error.message);
    }
    setLoading(false);
  };

  // Load data on component mount
  useEffect(() => {
    loadInventory();
    loadItems();
  }, []);

  return (
    <div className="p-6">
      <h2 className="text-2xl font-bold mb-4">Inventory Management</h2>
      
      {/* Success/Error message display */}
      {message && (
        <div className="mb-4 p-3 bg-blue-100 border border-blue-400 text-blue-700 rounded">
          {message}
        </div>
      )}

      {/* Add New Item to Catalog */}
      <div className="mb-6 p-4 border rounded">
        <h3 className="text-lg font-semibold mb-3">Add New Item</h3>
        <div className="flex gap-2 mb-2">
          <input
            type="text"
            placeholder="Item name"
            value={newItem.name}
            onChange={(e) => setNewItem({ ...newItem, name: e.target.value })}
            className="border px-3 py-2 rounded flex-1"
          />
          <button
            onClick={addItem}
            disabled={loading}
            className="bg-blue-500 text-white px-4 py-2 rounded hover:bg-blue-600 disabled:opacity-50"
          >
            Add Item
          </button>
        </div>
        <p className="text-sm text-gray-600 mt-2">
          This adds a new item to the catalog. To add stock, use the "Add to Inventory" section below.
        </p>
      </div>

      {/* Add Item to Inventory */}
      <div className="mb-6 p-4 border rounded">
        <h3 className="text-lg font-semibold mb-3">Add to Inventory</h3>
        <div className="flex gap-2 mb-2">
          <input
            type="number"
            placeholder="Item ID"
            value={newItem.itemId}
            onChange={(e) => setNewItem({ ...newItem, itemId: e.target.value })}
            className="border px-3 py-2 rounded w-24"
          />
          <input
            type="number"
            placeholder="Stock"
            value={newItem.stock}
            onChange={(e) => setNewItem({ ...newItem, stock: e.target.value })}
            className="border px-3 py-2 rounded w-24"
          />
          <input
            type="number"
            placeholder="Capacity"
            value={newItem.capacity}
            onChange={(e) => setNewItem({ ...newItem, capacity: e.target.value })}
            className="border px-3 py-2 rounded w-24"
          />
          <button
            onClick={addToInventory}
            disabled={loading}
            className="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600 disabled:opacity-50"
          >
            Add to Inventory
          </button>
        </div>
        <p className="text-sm text-gray-600 mt-2">
          Use this to add stock and capacity for an existing item. Item ID must exist in the catalog.
        </p>
      </div>

      {/* Update Inventory Item */}
      <div className="mb-6 p-4 border rounded">
        <h3 className="text-lg font-semibold mb-3">Update Inventory</h3>
        <div className="flex gap-2 mb-2">
          <input
            type="number"
            placeholder="Item ID"
            value={updateItem.itemId}
            onChange={(e) => setUpdateItem({ ...updateItem, itemId: e.target.value })}
            className="border px-3 py-2 rounded w-24"
          />
          <input
            type="number"
            placeholder="Stock"
            value={updateItem.stock}
            onChange={(e) => setUpdateItem({ ...updateItem, stock: e.target.value })}
            className="border px-3 py-2 rounded w-24"
          />
          <input
            type="number"
            placeholder="Capacity"
            value={updateItem.capacity}
            onChange={(e) => setUpdateItem({ ...updateItem, capacity: e.target.value })}
            className="border px-3 py-2 rounded w-24"
          />
          <button
            onClick={updateInventoryItem}
            disabled={loading}
            className="bg-green-500 text-white px-4 py-2 rounded hover:bg-green-600 disabled:opacity-50"
          >
            Update
          </button>
        </div>
      </div>

      {/* Items Catalog Table */}
      <div className="mb-6">
        <div className="flex justify-between items-center mb-3">
          <h3 className="text-lg font-semibold">Items Catalog</h3>
          <button
            onClick={loadItems}
            disabled={loading}
            className="bg-gray-500 text-white px-3 py-1 rounded hover:bg-gray-600 disabled:opacity-50"
          >
            Refresh
          </button>
        </div>
        
        <div className="border rounded">
          <table className="w-full">
            <thead className="bg-gray-50">
              <tr>
                <th className="px-4 py-2 text-left">ID</th>
                <th className="px-4 py-2 text-left">Name</th>
              </tr>
            </thead>
            <tbody>
              {items.map((item) => (
                <tr key={item.id} className="border-t">
                  <td className="px-4 py-2">{item.id}</td>
                  <td className="px-4 py-2">{item.name}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Current Inventory Table */}
      <div className="mb-6">
        <div className="flex justify-between items-center mb-3">
          <h3 className="text-lg font-semibold">Current Inventory</h3>
          <button
            onClick={loadInventory}
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
                  <th className="px-4 py-2 text-left">Stock</th>
                  <th className="px-4 py-2 text-left">Capacity</th>
                  <th className="px-4 py-2 text-left">Actions</th>
                </tr>
              </thead>
              <tbody>
                {inventory.map((item) => (
                  <tr key={item.id} className="border-t">
                    <td className="px-4 py-2">{item.id}</td>
                    <td className="px-4 py-2">{item.name}</td>
                    <td className="px-4 py-2">{item.stock}</td>
                    <td className="px-4 py-2">{item.capacity}</td>
                    <td className="px-4 py-2">
                      <button
                        onClick={() => deleteItem(item.id)}
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
    </div>
  );
};

export default Inventory;
