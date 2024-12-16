"use client"

import { useState } from "react"
import { toast } from "sonner"
import { Upload } from "lucide-react"

import { Button } from "@/components/ui/button"
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import { Input } from "@/components/ui/input"
import { startImport, checkImportStatus } from "@/lib/api/import-export"

interface ImportDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  onSuccess: () => void
}

export function ImportDialog({
  open,
  onOpenChange,
  onSuccess,
}: ImportDialogProps) {
  const [isLoading, setIsLoading] = useState(false)

  async function handleFileChange(event: React.ChangeEvent<HTMLInputElement>) {
    const file = event.target.files?.[0]
    if (!file) return

    setIsLoading(true)
    try {
      const { id } = await startImport(file)
      toast.info("Import started")
      
      // Poll for import status
      const interval = setInterval(async () => {
        const { status } = await checkImportStatus(id)
        if (status === "DONE") {
          clearInterval(interval)
          toast.success("Import completed")
          onSuccess()
          onOpenChange(false)
        } else if (status === "ERROR") {
          clearInterval(interval)
          toast.error("Import failed")
        }
      }, 1000)
    } catch (error) {
      toast.error("Failed to start import")
    } finally {
      setIsLoading(false)
    }
  }

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Import Sections</DialogTitle>
        </DialogHeader>
        <div className="space-y-4">
          <div className="flex items-center justify-center w-full">
            <label
              htmlFor="file-upload"
              className="flex flex-col items-center justify-center w-full h-32 border-2 border-dashed rounded-lg cursor-pointer hover:bg-secondary/50"
            >
              <div className="flex flex-col items-center justify-center pt-5 pb-6">
                <Upload className="w-8 h-8 mb-2 text-muted-foreground" />
                <p className="mb-2 text-sm text-muted-foreground">
                  Click to upload or drag and drop
                </p>
              </div>
              <Input
                id="file-upload"
                type="file"
                className="hidden"
                accept=".xlsx,.xls,.csv"
                onChange={handleFileChange}
                disabled={isLoading}
              />
            </label>
          </div>
        </div>
      </DialogContent>
    </Dialog>
  )
}