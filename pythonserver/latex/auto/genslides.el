(TeX-add-style-hook "genslides"
 (lambda ()
    (TeX-run-style-hooks
     "graphics"
     "fontspec"
     "tikzdefinitions"
     ""
     "geometry"
     "hmargin=20mm"
     "vmargin=30mm"
     "a4paper"
     "babel"
     "ngerman"
     "latex2e"
     "art12"
     "article"
     "12pt")))

