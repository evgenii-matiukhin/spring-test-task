"use client"

import { MoreHorizontal, Pencil, Trash } from "lucide-react"
import { toast } from "sonner"

import { Button } from "@/components/ui/button"
import {
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuItem,
  DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"
import { deleteSection } from "@/lib/api/sections"
import type { Section } from "@/lib/types"

export const columns = [
  {
    accessorKey: "name",
    header: "Name",
  },
  {
    accessorKey: "geologicalClasses",
    header: "Geological Classes",
    cell: ({ row }) => {
      const classes = row.original.geologicalClasses
      return classes.map(c => c.name).join(", ")
    },
  },
  {
    id: "actions",
    cell: ({ row }) => {
      const section = row.original

      return (
        <DropdownMenu>
          <DropdownMenuTrigger asChild>
            <Button variant="ghost" className="h-8 w-8 p-0">
              <MoreHorizontal className="h-4 w-4" />
            </Button>
          </DropdownMenuTrigger>
          <DropdownMenuContent align="end">
            <DropdownMenuItem
              onClick={() => {
                // Handle edit - implement this
              }}
            >
              <Pencil className="mr-2 h-4 w-4" />
              Edit
            </DropdownMenuItem>
            <DropdownMenuItem
              onClick={async () => {
                try {
                  await deleteSection(section.id)
                  toast.success("Section deleted")
                  // Refresh the table - implement this
                } catch (error) {
                  toast.error("Failed to delete section")
                }
              }}
            >
              <Trash className="mr-2 h-4 w-4" />
              Delete
            </DropdownMenuItem>
          </DropdownMenuContent>
        </DropdownMenu>
      )
    },
  },
]