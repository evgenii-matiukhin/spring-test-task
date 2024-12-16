"use client"

import { useEffect, useState } from "react"
import { Plus, FileDown, FileUp } from "lucide-react"
import { toast } from "sonner"

import { Button } from "@/components/ui/button"
import { DataTable } from "@/components/sections/data-table"
import { columns } from "@/components/sections/columns"
import { SectionDialog } from "@/components/sections/section-dialog"
import { ImportDialog } from "@/components/sections/import-dialog"
import { fetchSections } from "@/lib/api/sections"
import type { Section } from "@/lib/types"
import { startExport, checkExportStatus, downloadExportFile } from "@/lib/api/import-export"

export default function SectionsPage() {
  const [sections, setSections] = useState<Section[]>([])
  const [isLoading, setIsLoading] = useState(true)
  const [openDialog, setOpenDialog] = useState(false)
  const [openImportDialog, setOpenImportDialog] = useState(false)

  useEffect(() => {
    loadSections()
  }, [])

  async function loadSections() {
    try {
      const data = await fetchSections()
      setSections(data)
    } catch (error) {
      toast.error("Failed to load sections")
    } finally {
      setIsLoading(false)
    }
  }

  async function handleExport() {
    try {
      const { id } = await startExport()
      toast.info("Export started")
      
      // Poll for export status
      const interval = setInterval(async () => {
        const { status } = await checkExportStatus(id)
        if (status === "DONE") {
          clearInterval(interval)
          const blob = await downloadExportFile(id)
          const url = window.URL.createObjectURL(blob)
          const a = document.createElement('a')
          a.href = url
          a.download = 'sections-export.xlsx'
          document.body.appendChild(a)
          a.click()
          window.URL.revokeObjectURL(url)
          document.body.removeChild(a)
          toast.success("Export completed")
        } else if (status === "ERROR") {
          clearInterval(interval)
          toast.error("Export failed")
        }
      }, 1000)
    } catch (error) {
      toast.error("Failed to start export")
    }
  }

  return (
    <div className="container mx-auto py-10">
      <div className="flex justify-between items-center mb-8">
        <h1 className="text-3xl font-bold">Sections</h1>
        <div className="flex gap-4">
          <Button onClick={() => setOpenImportDialog(true)}>
            <FileUp className="mr-2 h-4 w-4" />
            Import
          </Button>
          <Button onClick={handleExport}>
            <FileDown className="mr-2 h-4 w-4" />
            Export
          </Button>
          <Button onClick={() => setOpenDialog(true)}>
            <Plus className="mr-2 h-4 w-4" />
            Add Section
          </Button>
        </div>
      </div>

      <DataTable columns={columns} data={sections} />

      <SectionDialog
        open={openDialog}
        onOpenChange={setOpenDialog}
        onSuccess={loadSections}
      />

      <ImportDialog
        open={openImportDialog}
        onOpenChange={setOpenImportDialog}
        onSuccess={loadSections}
      />
    </div>
  )
}