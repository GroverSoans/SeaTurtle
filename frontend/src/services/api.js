// Base URL for API calls - uses environment variable or defaults to localhost
const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:4567';

export const api = {
  // Items endpoints - manage the catalog of available items
  async getAllItems() {
    const response = await fetch(`${API_BASE_URL}/items`);
    return response.json();
  },

  // Inventory endpoints - manage stock levels and capacity
  async getAllInventory() {
    const response = await fetch(`${API_BASE_URL}/inventory`);
    return response.json();
  },

  async getOutOfStock() {
    const response = await fetch(`${API_BASE_URL}/inventory/out-of-stock`);
    return response.json();
  },

  async getOverstocked() {
    const response = await fetch(`${API_BASE_URL}/inventory/overstocked`);
    return response.json();
  },

  async getLowStock() {
    const response = await fetch(`${API_BASE_URL}/inventory/low-stock`);
    return response.json();
  },

  async getInventoryItem(id) {
    const response = await fetch(`${API_BASE_URL}/inventory/${id}`);
    return response.json();
  },

  async addNewItem(name) {
    const response = await fetch(`${API_BASE_URL}/items`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name })
    });
    return response.json();
  },

  async addToInventory(itemId, stock, capacity) {
    const response = await fetch(`${API_BASE_URL}/inventory`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ itemId, stock, capacity })
    });
    return response.json();
  },

  async updateInventoryItem(itemId, stock, capacity) {
    const response = await fetch(`${API_BASE_URL}/inventory`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ itemId, stock, capacity })
    });
    return response.json();
  },

  async deleteInventoryItem(id) {
    const response = await fetch(`${API_BASE_URL}/inventory/${id}`, {
      method: 'DELETE'
    });
    return response.json();
  },

  // Distributor endpoints - manage distributors and their pricing
  async getAllDistributors() {
    const response = await fetch(`${API_BASE_URL}/distributors`);
    return response.json();
  },

  async getDistributorItems(id) {
    const response = await fetch(`${API_BASE_URL}/distributors/${id}/items`);
    return response.json();
  },

  async getItemOfferings(id) {
    const response = await fetch(`${API_BASE_URL}/items/${id}/offerings`);
    return response.json();
  },

  async addDistributor(name) {
    const response = await fetch(`${API_BASE_URL}/distributors`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ name })
    });
    return response.json();
  },

  async addItemToDistributor(distributorId, itemId, cost) {
    const response = await fetch(`${API_BASE_URL}/distributors/${distributorId}/items`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ itemId, cost })
    });
    return response.json();
  },

  async updateItemPrice(distributorId, itemId, cost) {
    const response = await fetch(`${API_BASE_URL}/distributors/${distributorId}/items/${itemId}/price`, {
      method: 'PUT',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ cost })
    });
    return response.json();
  },

  async getRestockPrice(itemId, quantity) {
    const response = await fetch(`${API_BASE_URL}/items/${itemId}/restock-price?quantity=${quantity}`);
    return response.json();
  },

  async deleteDistributor(id) {
    const response = await fetch(`${API_BASE_URL}/distributors/${id}`, {
      method: 'DELETE'
    });
    return response.json();
  },

  // Export endpoint - download data in CSV format
  async exportTable(tableName) {
    const response = await fetch(`${API_BASE_URL}/export/${tableName}`);
    if (response.ok) {
      const blob = await response.blob();
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `${tableName}.csv`;
      a.click();
      window.URL.revokeObjectURL(url);
    } else {
      return response.json();
    }
  }
};
