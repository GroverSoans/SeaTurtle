/** @type {import('next').NextConfig} */
const nextConfig = {
  output: 'standalone',
  experimental: {
    outputFileTracingRoot: undefined,
  },
  // Remove the rewrites since we're using environment variables
  // async rewrites() {
  //   return [
  //     {
  //       source: '/api/:path*',
  //       destination: 'http://backend:4567/:path*',
  //     },
  //   ];
  // },
}

module.exports = nextConfig
