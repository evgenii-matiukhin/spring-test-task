"use client"

import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog"
import { SectionForm } from "./section-form"
import type { Section } from "@/lib/types"

interface SectionDialogProps {
  open: boolean
  onOpenChange: (open: boolean) => void
  section?: Section
  onSuccess: () => void
}

export function SectionDialog({
  open,
  onOpenChange,
  section,
  onSuccess,
}: SectionDialogProps) {
  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{section ? "Edit Section" : "Add Section"}</DialogTitle>
        </DialogHeader>
        <SectionForm
          section={section}
          onSuccess={() => {
            onSuccess()
            onOpenChange(false)
          }}
          onCancel={() => onOpenChange(false)}
        />
      </DialogContent>
    </Dialog>
  )
}