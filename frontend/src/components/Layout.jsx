import React from 'react';
import SideNavbar from './SideNavbar';
import Header from './Header';
import MainContent from './MainContent';

const Layout = () => {
  return (
    <div className="flex h-screen bg-gray-100">
      {/* Sidebar */}
      <SideNavbar />
      
      {/* Main Content Area */}
      <div className="flex-1 flex flex-col overflow-hidden">
        {/* Header */}
        <Header />
        
        {/* Main Content */}
        <MainContent />
      </div>
    </div>
  );
};

export default Layout;
