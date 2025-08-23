# MailMind - AI Email Reply Generator

## Overview

MailMind is a modern web application that leverages artificial intelligence to generate professional email replies. The application analyzes incoming email content and produces contextually appropriate responses with customizable tone settings. Built with a full-stack TypeScript architecture, it provides a clean, intuitive interface for users to paste email content, select desired tone, and receive AI-generated replies that can be copied and used immediately.

## User Preferences

Preferred communication style: Simple, everyday language.

## System Architecture

### Frontend Architecture
The client-side application is built using **React 18** with **TypeScript** and follows a component-based architecture:

- **UI Framework**: React with functional components and hooks
- **Styling**: Tailwind CSS with shadcn/ui component library for consistent design
- **Routing**: Wouter for lightweight client-side routing
- **State Management**: React Hook Form for form handling, TanStack Query for server state
- **Component Library**: Radix UI primitives with custom styling via class-variance-authority

The frontend uses a modern build setup with **Vite** for fast development and optimized production builds. The component structure follows atomic design principles with reusable UI components in the `components/ui` directory.

### Backend Architecture
The server is built with **Express.js** and TypeScript:

- **Runtime**: Node.js with ESM modules
- **API Framework**: Express.js with middleware for JSON parsing and request logging
- **Architecture Pattern**: RESTful API design with route separation
- **Storage Interface**: Abstract storage pattern with in-memory implementation (MemStorage)
- **Development Tools**: Hot reload with tsx, middleware for request/response logging

The backend follows a clean architecture with separated concerns - routes handle HTTP logic while storage classes manage data persistence.

### Data Storage Solutions
Currently implements an **in-memory storage system** with an abstract interface:

- **Storage Pattern**: Interface-based design (`IStorage`) allowing for easy swapping of storage implementations
- **Current Implementation**: In-memory storage using JavaScript Maps
- **Database Schema**: Drizzle ORM configured for PostgreSQL (ready for migration from in-memory)
- **Schema Definition**: User entities with username/password fields, prepared for database integration

The architecture is designed to easily transition from in-memory storage to PostgreSQL when needed.

### Authentication and Authorization
The application structure includes user management capabilities:

- **User Schema**: Defined with Drizzle ORM including unique usernames and passwords
- **Session Management**: Express session configuration prepared (connect-pg-simple for PostgreSQL sessions)
- **Current State**: User management interface implemented, ready for authentication integration

### Development and Build System
**Development Environment**:
- **Build Tool**: Vite with React plugin for fast HMR and optimized builds
- **TypeScript**: Strict configuration with path mapping for clean imports
- **Development Server**: Concurrent client/server development with request logging

**Production Build**:
- **Client**: Vite build process generating optimized static assets
- **Server**: esbuild bundling for Node.js deployment
- **Asset Management**: Static file serving with Express in production

## External Dependencies

### Core Framework Dependencies
- **@neondatabase/serverless**: PostgreSQL serverless driver for database connections
- **drizzle-orm**: Type-safe ORM for database operations and schema management
- **drizzle-kit**: Database migration and schema management tools

### Frontend UI Libraries
- **@radix-ui/react-***: Complete suite of unstyled, accessible UI primitives (dialogs, dropdowns, forms, etc.)
- **@tanstack/react-query**: Server state management and data fetching
- **react-hook-form**: Form handling with validation
- **@hookform/resolvers**: Integration between react-hook-form and validation libraries

### Styling and Design
- **tailwindcss**: Utility-first CSS framework
- **class-variance-authority**: Component variant styling system
- **lucide-react**: Modern icon library
- **cmdk**: Command palette component

### Development Tools
- **@replit/vite-plugin-runtime-error-modal**: Enhanced error reporting in development
- **@replit/vite-plugin-cartographer**: Development environment integration for Replit

### Utility Libraries
- **clsx**: Conditional className utility
- **date-fns**: Date manipulation and formatting
- **nanoid**: Unique ID generation
- **zod**: TypeScript-first schema validation

The application is architected for scalability with clear separation of concerns, making it easy to extend with additional features like real AI integration, advanced authentication, and enhanced email processing capabilities.